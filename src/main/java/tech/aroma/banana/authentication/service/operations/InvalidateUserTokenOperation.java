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

import javax.inject.Inject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.authentication.service.InvalidateUserTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateUserTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.banana.authentication.service.AuthenticationAssertions.checkNotNull;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.checkRequestNotNull;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@Internal
final class InvalidateUserTokenOperation implements ThriftOperation<InvalidateUserTokenRequest, InvalidateUserTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(InvalidateUserTokenOperation.class);

    private final TokenRepository repository;

    @Inject
    InvalidateUserTokenOperation(TokenRepository repository)
    {
        checkThat(repository).is(notNull());

        this.repository = repository;
    }

    @Override
    public InvalidateUserTokenResponse process(InvalidateUserTokenRequest request) throws TException
    {
        LOG.debug("Received request to invalidate token: {}", request);
        checkRequestNotNull(request);
        checkNotNull(request.token, "missing token");

        String tokenId = request.token.tokenId;
        checkThat(tokenId)
            .throwing(ex -> new InvalidArgumentException("missing tokenId"))
            .is(nonEmptyString());
        tryDelete(tokenId);

        return new InvalidateUserTokenResponse();
    }

    private void tryDelete(String tokenId) throws TException
    {
        try
        {
            repository.deleteToken(tokenId);
        }
        catch (TException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new OperationFailedException("Failed to delete token from repository: " + ex.getMessage());
        }

    }

    @Override
    public String toString()
    {
        return "InvalidateUserTokenOperation{" + "repository=" + repository + '}';
    }

}
