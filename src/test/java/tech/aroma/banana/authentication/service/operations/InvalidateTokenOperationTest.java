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
import tech.aroma.banana.thrift.authentication.ApplicationToken;
import tech.aroma.banana.thrift.authentication.UserToken;
import tech.aroma.banana.thrift.authentication.service.AuthenticationToken;
import tech.aroma.banana.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.aroma.banana.thrift.functions.TokenFunctions;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.BooleanGenerators.booleans;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class InvalidateTokenOperationTest
{

    @Mock
    private TokenRepository repository;

    @GeneratePojo
    private InvalidateTokenRequest request;
    
    @GeneratePojo
    private ApplicationToken applicationToken;
    
    private AuthenticationToken authenticationToken;
    
    @GeneratePojo
    private UserToken userToken;
    
    private String tokenId;

    private InvalidateTokenOperation instance;

    @Before
    public void setUp()
    {
        instance = new InvalidateTokenOperation(repository);
        verifyZeroInteractions(repository);

        authenticationToken = new AuthenticationToken();

        boolean heads = one(booleans());

        if (heads)
        {
            authenticationToken.setApplicationToken(applicationToken);
        }
        else
        {
            authenticationToken.setUserToken(userToken);
        }

        request.setToken(authenticationToken);

        tokenId = TokenFunctions.extractTokenId(request.token);
    }

    @Test
    public void testConstructor()
    {
        assertThrows(() -> new InvalidateTokenOperation(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Repeat(200)
    @Test
    public void testProcess() throws Exception
    {
        InvalidateTokenResponse response = instance.process(request);
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

    @Test
    public void testWithMissingToken() throws Exception
    {
        request.token = null;

        assertThrows(() -> instance.process(request))
            .isInstanceOf(InvalidArgumentException.class);
        
        request.token = new AuthenticationToken();

        assertThrows(() -> instance.process(request))
            .isInstanceOf(InvalidArgumentException.class);
        
    }

    @Test
    public void testWithMissingTokenId() throws Exception
    {
        if (request.token.isSetApplicationToken())
        {
            request.token.getApplicationToken().setTokenId("");
        }
        else if (request.token.isSetUserToken())
        {
            request.token.getUserToken().setTokenId("");
        }

        assertThrows(() -> instance.process(request))
            .isInstanceOf(InvalidArgumentException.class);
    }

}
