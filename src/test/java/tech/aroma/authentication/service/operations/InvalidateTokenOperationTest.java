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

package tech.aroma.authentication.service.operations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.aroma.data.TokenRepository;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.GenerateString;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static tech.aroma.authentication.service.TokenGenerators.authenticationTokens;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;
import static tech.sirwellington.alchemy.test.junit.runners.GenerateString.Type.UUID;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class InvalidateTokenOperationTest
{

    @Mock
    private TokenRepository tokenRepo;

    @GeneratePojo
    private InvalidateTokenRequest request;
    
    @GeneratePojo
    private AuthenticationToken authenticationToken;

    @GenerateString(UUID)
    private String tokenId;
    
    @GenerateString(UUID)
    private String ownerId;

    private InvalidateTokenOperation instance;

    @Before
    public void setUp()
    {
        instance = new InvalidateTokenOperation(tokenRepo);
        verifyZeroInteractions(tokenRepo);

        authenticationToken = one(authenticationTokens());
        authenticationToken.tokenId = tokenId;
        authenticationToken.ownerId = ownerId;
        
        request.setToken(authenticationToken);
        request.unsetBelongingTo();
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

        verify(tokenRepo).deleteToken(tokenId);
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
            .when(tokenRepo)
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
       
        AuthenticationToken missingTokenId = new AuthenticationToken(authenticationToken);
        missingTokenId.unsetTokenId();

        assertThrows(() -> instance.process(new InvalidateTokenRequest(missingTokenId)))
            .isInstanceOf(InvalidArgumentException.class);
    }
    
    @Test
    public void testWithDeleteBelongingTo() throws Exception
    {
        request.belongingTo = ownerId;
        request.unsetToken();
        request.unsetMultipleTokens();
        
        InvalidateTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());
        
        verify(tokenRepo).deleteTokensBelongingTo(ownerId);
    }

}
