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
import tech.aroma.banana.authentication.service.AuthenticationAssertions;
import tech.aroma.banana.authentication.service.data.Token;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.LengthOfTime;
import tech.aroma.banana.thrift.TimeUnit;
import tech.aroma.banana.thrift.authentication.UserToken;
import tech.aroma.banana.thrift.authentication.service.CreateUserTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateUserTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.NumberAssertions.greaterThan;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.stringWithLengthGreaterThan;

/**
 *
 * @author SirWellington
 */
@Internal
final class CreateUserTokenOperation implements ThriftOperation<CreateUserTokenRequest, CreateUserTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(CreateUserTokenOperation.class);
    private final static LengthOfTime DEFAULT_LIFETIME = new LengthOfTime(TimeUnit.DAYS, 1);

    private final TokenCreator tokenCreator;
    private final TokenRepository repository;
    private final Function<LengthOfTime, Duration> lengthOfTimeConverter;

    @Inject
    CreateUserTokenOperation(TokenCreator tokenCreator,
                             TokenRepository repository,
                             Function<LengthOfTime, Duration> lengthOfTimeConverter)
    {
        checkThat(tokenCreator, repository, lengthOfTimeConverter)
            .are(notNull());
        
        this.tokenCreator = tokenCreator;
        this.repository = repository;
        this.lengthOfTimeConverter = lengthOfTimeConverter;
    }

    @Override
    public CreateUserTokenResponse process(CreateUserTokenRequest request) throws TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        checkThat(request.userId)
            .throwing(ex -> new InvalidArgumentException("bad userId"))
            .is(nonEmptyString())
            .is(stringWithLengthGreaterThan(3));

        LOG.debug("Received request to create an User Token: {}", request);

        String tokenId = tokenCreator.create();
        Instant tokenCreation = Instant.now();
        checkThat(tokenId)
            .throwing(OperationFailedException.class)
            .is(nonEmptyString());

        Token token = new Token();
        token.setTokenId(tokenId);
        token.setOwnerId(request.userId);
        token.setTimeOfCreation(tokenCreation);
        
        Duration tokenDuration = getDurationFrom(request);
        token.setTimeOfExpiration(tokenCreation.plus(tokenDuration));
        repository.saveToken(token);

        UserToken userToken = toUserToken(token);

        return new CreateUserTokenResponse().setToken(userToken);
    }

    private Duration getDurationFrom(CreateUserTokenRequest request) throws InvalidArgumentException
    {

        if (!request.isSetLifetime())
        {
            LOG.warn("Length of time not set. Defaulting to: {}", DEFAULT_LIFETIME);
            request.setLifetime(DEFAULT_LIFETIME);
        }

        checkThat(request.lifetime.value)
            .throwing(ex -> new InvalidArgumentException("Token Lifetime must be > 0"))
            .is(greaterThan(0L));

        return lengthOfTimeConverter.apply(request.lifetime);
    }

    private UserToken toUserToken(Token token)
    {
        return token.asUserToken();
    }

    @Override
    public String toString()
    {
        return "CreateUserTokenOperation{" + "tokenCreator=" + tokenCreator + ", tokenRepository=" + repository + ", lengthOfTimeConverter=" + lengthOfTimeConverter + '}';
    }

}
