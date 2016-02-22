
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
import tech.aroma.data.TokenRepository;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.thrift.assertions.AromaAssertions.checkRequestNotNull;
import static tech.aroma.thrift.assertions.AromaAssertions.legalToken;
import static tech.aroma.thrift.assertions.AromaAssertions.withMessage;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.validUUID;

/**
 *
 * @author SirWellington
 */
@Internal
final class InvalidateTokenOperation implements ThriftOperation<InvalidateTokenRequest, InvalidateTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(InvalidateTokenOperation.class);

    private final TokenRepository tokenRepo;

    @Inject
    InvalidateTokenOperation(TokenRepository repository)
    {
        checkThat(repository).is(notNull());

        this.tokenRepo = repository;
    }

    @Override
    public InvalidateTokenResponse process(InvalidateTokenRequest request) throws TException
    {
        LOG.debug("Received request to invalidate token: {}", request);

        checkRequestNotNull(request);

        if (request.isSetBelongingTo())
        {
            String ownerId = request.belongingTo;
            checkThat(ownerId)
                .throwing(InvalidArgumentException.class)
                .usingMessage("belongingToSet, but ownerId is missing")
                .is(nonEmptyString())
                .usingMessage("belongingToSet, but owner is not a valid UUID")
                .is(validUUID());

            deleteAllTokensBelongingTo(ownerId);
        }
        else
        {
            checkThat(request.token)
                .throwing(InvalidArgumentException.class)
                .usingMessage("request is missing token")
                .is(legalToken());

            deleteToken(request.token);
        }

        return new InvalidateTokenResponse();
    }

    private void deleteAllTokensBelongingTo(String ownerId) throws TException
    {
        tokenRepo.deleteTokensBelongingTo(ownerId);
    }

    private void deleteToken(AuthenticationToken token) throws InvalidArgumentException, TException
    {
        String tokenId = token.tokenId;
        checkThat(tokenId)
            .throwing(withMessage("missing tokenId"))
            .is(nonEmptyString());

        tryDelete(tokenId);
    }

    private void tryDelete(String tokenId) throws TException
    {
        try
        {
            tokenRepo.deleteToken(tokenId);
        }
        catch (Exception ex)
        {
            LOG.debug("Failed to delete token from Repository: {}", tokenId, ex);
            if (ex instanceof TException)
            {
                throw ex;
            }
            else
            {
                throw new OperationFailedException("Could not remove token: " + ex.getMessage());
            }
        }
    }

}
