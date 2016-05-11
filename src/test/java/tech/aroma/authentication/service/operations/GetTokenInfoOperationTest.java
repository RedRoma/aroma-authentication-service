/*
 * Copyright 2016 RedRoma, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.aroma.authentication.service.operations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.aroma.data.TokenRepository;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.service.GetTokenInfoRequest;
import tech.aroma.thrift.authentication.service.GetTokenInfoResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class GetTokenInfoOperationTest
{
    @Mock
    private TokenRepository repository;
    
    @GeneratePojo
    private GetTokenInfoRequest request;
    
    @GeneratePojo
    private AuthenticationToken token;
    
    private GetTokenInfoOperation instance;
    
    @Before
    public void setUp() throws Exception
    {
        instance = new GetTokenInfoOperation(repository);
        verifyZeroInteractions(repository);
        
        when(repository.getToken(token.getTokenId()))
            .thenReturn(token);
        
        request.setTokenId(token.getTokenId());
    }
    
    @Repeat(100)
    @Test
    public void testProcess() throws Exception
    {
        GetTokenInfoResponse response = instance.process(request);
        assertThat(response, notNullValue());
        assertThat(response.token, is(token));
    }
    
    @Test
    public void testProcessEdgeCases() throws Exception
    {
        assertThrows(() -> instance.process(null))
            .isInstanceOf(InvalidArgumentException.class);
    }
    
    @Test
    public void testWhenRepositoryFails() throws Exception
    {
        when(repository.getToken(request.tokenId))
            .thenThrow(new RuntimeException());
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
            
    }
    
    @Test
    public void testWhenRepositoryReturnsNull() throws Exception
    {
        when(repository.getToken(request.tokenId))
            .thenReturn(null);
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
    }
}
