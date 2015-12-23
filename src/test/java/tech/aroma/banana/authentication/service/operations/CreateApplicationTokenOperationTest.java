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
import tech.aroma.banana.authentication.service.data.Token;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.LengthOfTime;
import tech.aroma.banana.thrift.authentication.service.CreateApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateApplicationTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.aroma.banana.thrift.functions.TimeFunctions;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.GenerateString;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static java.time.Instant.now;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
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
public class CreateApplicationTokenOperationTest 
{

    private final Function<LengthOfTime, Duration> lengthOfTimeConverter = TimeFunctions.LENGTH_OF_TIME_TO_DURATION;
    
    @Mock
    private TokenCreator tokenCreator;
    
    @Mock
    private TokenRepository repository;

    private CreateApplicationTokenRequest request;

    private CreateApplicationTokenOperation instance;
    
    @GenerateString
    private String tokenId;
    
    @GenerateString
    private String applicationId;
    
    @GeneratePojo
    private LengthOfTime lifetime;
    
    @Captor
    private ArgumentCaptor<Token> tokenCaptor;
    
    @Before
    public void setUp()
    {
        instance = new CreateApplicationTokenOperation(lengthOfTimeConverter, tokenCreator, repository);
        verifyZeroInteractions(tokenCreator, repository);
        
        lifetime.setValue(one(longs(1, 100_000)));
        
        
        request = new CreateApplicationTokenRequest()
            .setLifetime(lifetime)
            .setApplicationId(applicationId);
        
        when(tokenCreator.create()).thenReturn(tokenId);
    }

    @Repeat(500)
    @Test
    public void testProcess() throws Exception
    {
        Instant now = now();
        
        CreateApplicationTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());
        
        verify(repository).saveToken(tokenCaptor.capture());
        
        Token savedToken = tokenCaptor.getValue();
        assertThat(savedToken, notNullValue());
        assertThat(savedToken.getTokenId(), is(tokenId));
        assertThat(savedToken.getOwnerId(), is(applicationId));
        
        Instant timeOfCreation = savedToken.getTimeOfCreation();
        Duration timeOfCreationDelta = Duration.between(now, timeOfCreation).abs();
        assertThat(timeOfCreationDelta.getSeconds(), lessThanOrEqualTo(1L));
        
        Instant expectedTimeOfExpiration = now.plus(lengthOfTimeConverter.apply(lifetime));
        Instant timeOfExpiration = savedToken.getTimeOfExpiration();
        Duration timeOfExpirationDelta = Duration.between(timeOfExpiration, expectedTimeOfExpiration).abs();
        assertThat(timeOfExpirationDelta.getSeconds(), lessThan(1L));
    }
    
    @Test
    public void testProcessEdgeCases()
    {
        assertThrows(() -> instance.process(null))
            .isInstanceOf(InvalidArgumentException.class);
    }
    
    @Test
    public void testWhenTokenCreatorReturnsEmpty() throws Exception
    {
        when(tokenCreator.create())
            .thenReturn("");
        
        assertThrows(() -> instance.process(request))
            .isInstanceOf(OperationFailedException.class);
    }

}