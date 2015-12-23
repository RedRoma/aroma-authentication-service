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

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import tech.aroma.banana.authentication.service.data.Token;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.LengthOfTime;
import tech.aroma.banana.thrift.authentication.service.CreateUserTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateUserTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.aroma.banana.thrift.functions.TimeFunctions;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.GenerateString;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static java.time.Instant.now;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.longs;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class CreateUserTokenOperationTest 
{

    @Mock
    private TokenCreator tokenCreator;
    
    @Mock
    private TokenRepository tokenRepository;
    
    private Function<LengthOfTime, Duration> lengthOfTimeConverter = TimeFunctions.LENGTH_OF_TIME_TO_DURATION;
    
    @GenerateString
    private String tokenId;
    
    @GenerateString
    private String userId;
    
    @Captor
    private ArgumentCaptor<Token> tokenCaptor;
    
    @GeneratePojo
    private CreateUserTokenRequest request;
    
    private CreateUserTokenOperation instance;
    
    @Before
    public void setUp()
    {
        instance = new CreateUserTokenOperation(tokenCreator, tokenRepository, lengthOfTimeConverter);
        verifyZeroInteractions(tokenCreator, tokenRepository);
        
        when(tokenCreator.create()).thenReturn(tokenId);
        
        request.setUserId(userId)
            .getLifetime()
            .setValue(one(longs(1, 1000000)));
    }
    
    @Test
    public void testWithBadConstructorArgs()
    {
        assertThrows(() -> new CreateUserTokenOperation(null, tokenRepository, lengthOfTimeConverter))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> new CreateUserTokenOperation(tokenCreator, null, lengthOfTimeConverter))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> new CreateUserTokenOperation(tokenCreator, tokenRepository, null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Repeat(500)
    @Test
    public void testProcess() throws Exception
    {
        Instant now = now();
        
        CreateUserTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());

        verify(tokenRepository).saveToken(tokenCaptor.capture());
        Token token = tokenCaptor.getValue();
        assertThat(token, notNullValue());
        assertThat(token.getOwnerId(), is(userId));
        assertThat(token.getTokenId(), is(tokenId));
        
        Duration timeOfCreationDelta = Duration.between(now, token.getTimeOfCreation()).abs();
        assertThat(timeOfCreationDelta.getSeconds(), is(lessThanOrEqualTo(1L)));
        
        Duration tokenLifetime = lengthOfTimeConverter.apply(request.lifetime);
        Instant expectedExpiration = now.plus(tokenLifetime);
        
        Duration timeOfExpirationDelta = Duration.between(token.getTimeOfExpiration(), expectedExpiration);
        assertThat(timeOfExpirationDelta.getSeconds(), is(lessThanOrEqualTo(1L)));
    }

    @Test
    public void testProcessEdgeCases()
    {
        assertThrows(() -> instance.process(null))
            .isInstanceOf(InvalidArgumentException.class);
    }
    
    @Test
    public void testWhenTokenCreatorReturnsNull()
    {
        when(tokenCreator.create())
            .thenReturn("");
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
    }

    @Test
    public void testWhenRepositoryFails() throws Exception
    {
        doThrow(new OperationFailedException())
            .when(tokenRepository)
            .saveToken(Mockito.any());
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
    }
}