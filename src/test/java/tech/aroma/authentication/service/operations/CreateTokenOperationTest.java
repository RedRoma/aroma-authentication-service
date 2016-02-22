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

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import tech.aroma.authentication.service.data.TokenCreator;
import tech.aroma.data.TokenRepository;
import tech.aroma.thrift.LengthOfTime;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.service.CreateTokenRequest;
import tech.aroma.thrift.authentication.service.CreateTokenResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.aroma.thrift.functions.TimeFunctions;
import tech.sirwellington.alchemy.annotations.testing.TimeSensitive;
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
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.TimeAssertions.epochNowWithinDelta;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.longs;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;
import static tech.sirwellington.alchemy.test.junit.runners.GenerateString.Type.UUID;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class CreateTokenOperationTest 
{

    private final Function<LengthOfTime, Duration> lengthOfTimeConverter = TimeFunctions.LENGTH_OF_TIME_TO_DURATION;
    
    @Mock
    private TokenCreator tokenCreator;
    
    @Mock
    private TokenRepository repository;

    @GeneratePojo
    private CreateTokenRequest request;

    private CreateTokenOperation instance;
    
    @GenerateString(UUID )
    private String tokenId;
    
    @GenerateString(UUID)
    private String ownerId;
    
    @Captor
    private ArgumentCaptor<AuthenticationToken> tokenCaptor;
    
    
    @Before
    public void setUp()
    {
        instance = new CreateTokenOperation(lengthOfTimeConverter, tokenCreator, repository);
        verifyZeroInteractions(tokenCreator, repository);
        
        request.lifetime.setValue(one(longs(1, 100_000)));
        request.setOwnerId(ownerId);
        
        when(tokenCreator.create()).thenReturn(tokenId);
    }

    @TimeSensitive
    @Repeat(500)
    @Test
    public void testProcess() throws Exception
    {
        Instant now = now();
        
        CreateTokenResponse response = instance.process(request);
        assertThat(response, notNullValue());
        
        verify(repository).saveToken(tokenCaptor.capture());
        
        AuthenticationToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken, notNullValue());
        assertThat(savedToken.getTokenId(), is(tokenId));
        assertThat(savedToken.getOwnerId(), is(ownerId));
        
        long timeOfCreation = savedToken.getTimeOfCreation();
        checkThat(timeOfCreation)
            .throwing(AssertionFailedError.class)
            .usingMessage("token timeOfCreation is off: " + Instant.ofEpochSecond(timeOfCreation))
            .is(epochNowWithinDelta(2000));
        
        Instant expectedTimeOfExpiration = now.plus(lengthOfTimeConverter.apply(request.lifetime));
        Instant timeOfExpiration = Instant.ofEpochMilli(savedToken.getTimeOfExpiration());
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
