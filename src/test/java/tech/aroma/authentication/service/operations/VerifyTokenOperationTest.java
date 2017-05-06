/*
 * Copyright 2017 RedRoma, Inc.
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

import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import tech.aroma.data.TokenRepository;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.TokenStatus;
import tech.aroma.thrift.authentication.service.VerifyTokenRequest;
import tech.aroma.thrift.authentication.service.VerifyTokenResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.InvalidTokenException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.GenerateString;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static tech.aroma.thrift.authentication.TokenStatus.EXPIRED;
import static tech.aroma.thrift.generators.TokenGenerators.authenticationTokens;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.TimeGenerators.pastInstants;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;
import static tech.sirwellington.alchemy.test.junit.runners.GenerateString.Type.UUID;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class VerifyTokenOperationTest
{

    @Mock
    private TokenRepository repository;

    private AuthenticationToken token;
    
    @GeneratePojo
    private VerifyTokenRequest request;
    
    @GenerateString(UUID)
    private String tokenId;
    
    @GenerateString(UUID)
    private String ownerId;
    
    private VerifyTokenOperation instance;

    @Captor
    private ArgumentCaptor<AuthenticationToken> captor;
    
    @Before
    public void setUp() throws TException
    {
        instance = new VerifyTokenOperation(repository);
        verifyZeroInteractions(repository);
        
        token = one(authenticationTokens());
        request.tokenId = tokenId;
        request.ownerId = ownerId;
        token.tokenId = tokenId;
        token.ownerId = ownerId;
        
        when(repository.containsToken(tokenId)).thenReturn(true);
        when(repository.doesTokenBelongTo(tokenId, ownerId)).thenReturn(true);
        when(repository.getToken(tokenId)).thenReturn(token);
    }

    @Test
    public void testConstructor()
    {
        assertThrows(() -> new VerifyTokenOperation(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Repeat(100)
    @Test
    public void testProcess() throws Exception
    {
        VerifyTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());
    }
    
    @Repeat
    @Test
    public void testWhenNoOwnerInRequest() throws Exception
    {
        request.unsetOwnerId();
        
        VerifyTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());
        
        verify(repository).getToken(tokenId);
        verify(repository, never()).doesTokenBelongTo(eq(tokenId), Mockito.any());
    }
    
    @Repeat
    @Test
    public void testWhenTokenOwnerMismatch() throws Exception
    {
        when(repository.doesTokenBelongTo(tokenId, ownerId))
            .thenReturn(false);
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(InvalidTokenException.class);
    }
    
    @Repeat(100)
    @Test
    public void testWhenTokenDoesNotExist() throws Exception
    {
        when(repository.containsToken(tokenId))
            .thenReturn(false);
        
        when(repository.getToken(tokenId))
            .thenThrow(new InvalidTokenException());

        request.unsetOwnerId();

        assertThrows(() -> instance.process(request))
            .isInstanceOf(InvalidTokenException.class);
    }
    
    @Repeat
    @Test
    public void testWhenRepositoryFails() throws Exception
    {
        when(repository.doesTokenBelongTo(tokenId, ownerId))
            .thenThrow(new RuntimeException());
            
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
    }

    @Test
    public void testWithBadRequests() throws Exception
    {
        assertThrows(() -> instance.process(null))
            .isInstanceOf(InvalidArgumentException.class);
        
        VerifyTokenRequest badRequest = new VerifyTokenRequest();
        
        assertThrows(() -> instance.process(badRequest))
            .isInstanceOf(InvalidArgumentException.class);
        
        badRequest.ownerId = ownerId;
        assertThrows(() -> instance.process(badRequest))
            .isInstanceOf(InvalidArgumentException.class);
        
    }
    
    @Test
    public void testWhenTokenExpired() throws Exception
    {
        token.setStatus(TokenStatus.EXPIRED);
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(InvalidTokenException.class);
    }
    
    @Test
    public void testWhenTokenHasRecentlyExpired() throws Exception
    {
        long pastTimestamp = one(pastInstants()).toEpochMilli();
        token.setTimeOfExpiration(pastTimestamp);
        
        assertThrows(() -> instance.process(request));
        
        verify(repository).saveToken(captor.capture());
        
        AuthenticationToken savedToken = captor.getValue();
        assertThat(savedToken, is(token));
        assertThat(savedToken.status, is(EXPIRED));
        
    }

}
