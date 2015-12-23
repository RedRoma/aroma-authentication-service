/*
 * Copyright 2015 Aroma Tech.
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

package tech.aroma.banana.authentication.service.operations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.authentication.service.InvalidateApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateApplicationTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class InvalidateApplicationTokenOperationTest 
{
    @Mock
    private TokenRepository repository;
    
    @GeneratePojo
    private InvalidateApplicationTokenRequest request;
    private String tokenId;
    
    private InvalidateApplicationTokenOperation instance;

    @Before
    public void setUp()
    {
        instance = new InvalidateApplicationTokenOperation(repository);
        verifyZeroInteractions(repository);
        
        tokenId = request.token.tokenId;
    }
    
    @Test
    public void testConstructor()
    {
        assertThrows(() -> new InvalidateApplicationTokenOperation(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Repeat(200)
    @Test
    public void testProcess() throws Exception
    {
        InvalidateApplicationTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());
        
        verify(repository).deleteToken(tokenId);
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
        doThrow(new RuntimeException())
            .when(repository)
            .deleteToken(tokenId);
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
    }

}