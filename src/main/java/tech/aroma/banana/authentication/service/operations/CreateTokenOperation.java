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
import javax.inject.Inject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.authentication.service.data.Token;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.LengthOfTime;
import tech.aroma.banana.thrift.TimeUnit;
import tech.aroma.banana.thrift.authentication.service.CreateTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateTokenResponse;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static java.time.Instant.now;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.checkRequestNotNull;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.withMessage;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.stringWithLengthGreaterThanOrEqualTo;

/**
 *
 * @author SirWellington
 */
@Internal
final class CreateTokenOperation implements ThriftOperation<CreateTokenRequest, CreateTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(CreateTokenOperation.class);
    private final static LengthOfTime DEFAULT_LIFETIME = new LengthOfTime(TimeUnit.DAYS, 30);

    private final Function<LengthOfTime, Duration> lengthOfTimeConverter;
    private final TokenCreator tokenCreator;
    private final TokenRepository repository;

    @Inject
    CreateTokenOperation(Function<LengthOfTime, Duration> lengthOfTimeConverter,
                                    TokenCreator tokenCreator,
                                    TokenRepository repository)
    {
        checkThat(lengthOfTimeConverter, tokenCreator, repository)
            .are(notNull());

        this.lengthOfTimeConverter = lengthOfTimeConverter;
        this.tokenCreator = tokenCreator;
        this.repository = repository;
    }

    @Override
    public CreateTokenResponse process(CreateTokenRequest request) throws TException
    {
        LOG.debug("Received request to create an  Token: {}", request);

        checkRequestNotNull(request);
        
        checkThat(request.desiredTokenType)
            .throwing(withMessage("Token Type is required"))
            .is(notNull());
        
        checkThat(request.ownerId)
            .throwing(withMessage("bad owner ID"))
            .is(nonEmptyString())
            .is(stringWithLengthGreaterThanOrEqualTo(3));
        
        if (!request.isSetLifetime())
        {
            LOG.info(" Token Lifetime not set. Defaulting to {}", DEFAULT_LIFETIME);
            request.setLifetime(DEFAULT_LIFETIME);
        }
        
        String tokenId = tokenCreator.create();
        Instant timeOfCreation = now();
        checkThat(tokenId)
            .throwing(OperationFailedException.class)
            .is(nonEmptyString());

        Token token = new Token();
        token.setTokenId(tokenId);
        token.setOwnerId(request.ownerId);
        token.setTimeOfCreation(timeOfCreation);
        token.setTokenType(request.desiredTokenType);
        
        Duration tokenLifetime = lengthOfTimeConverter.apply(request.lifetime);
        Instant timeOfExpiration = timeOfCreation.plus(tokenLifetime);
        token.setTimeOfExpiration(timeOfExpiration);
        
        repository.saveToken(token);
        LOG.debug("Saved token to repository: {}", token);
        
        return new CreateTokenResponse()
            .setToken(token.asAuthenticationToken());
    }
    
    @Override
    public String toString()
    {
        return "CreateTokenOperation{" + "lengthOfTimeConverter=" + lengthOfTimeConverter + ", tokenCreator=" + tokenCreator + ", tokenRepository=" + repository + '}';
    }
    
}
