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

import com.google.common.base.Strings;
import javax.inject.Inject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.authentication.service.VerifyTokenRequest;
import tech.aroma.banana.thrift.authentication.service.VerifyTokenResponse;
import tech.aroma.banana.thrift.exceptions.InvalidTokenException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.banana.authentication.service.AuthenticationAssertions.checkRequestNotNull;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.tokenInRepository;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.withMessage;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@Internal
final class VerifyTokenOperation implements ThriftOperation<VerifyTokenRequest, VerifyTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(VerifyTokenOperation.class);

    private final TokenRepository repository;

    @Inject
    VerifyTokenOperation(TokenRepository repository)
    {
        checkThat(repository).is(notNull());

        this.repository = repository;
    }

    @Override
    public VerifyTokenResponse process(VerifyTokenRequest request) throws TException
    {
        LOG.debug("Received request to verify an  token: {}", request);

        checkRequestNotNull(request);

        String tokenId = request.tokenId;
        checkThat(tokenId)
            .throwing(withMessage("missing tokenId"))
            .is(nonEmptyString())
            .throwing(InvalidTokenException.class)
            .is(tokenInRepository(repository));

        if (shouldCheckAgainstOwner(request))
        {
            String ownerId = request.ownerId;
            ensureTokenAndOwnerMatch(tokenId, ownerId);
        }

        return new VerifyTokenResponse();

    }

    private boolean shouldCheckAgainstOwner(VerifyTokenRequest request)
    {
        return request.isSetOwnerId() && !Strings.isNullOrEmpty(request.ownerId);
    }

    private void ensureTokenAndOwnerMatch(String tokenId, String ownerId) throws OperationFailedException, InvalidTokenException
    {

        boolean tokenAndOwnerMatch = tryDetermineMatch(tokenId, ownerId);

        if (!tokenAndOwnerMatch)
        {
            throw new InvalidTokenException();
        }
    }

    private boolean tryDetermineMatch(String tokenId, String ownerId) throws OperationFailedException
    {
        boolean match;

        try
        {
            match = repository.doesTokenBelongTo(tokenId, ownerId);
        }
        catch (Exception ex)
        {
            throw new OperationFailedException("Could not read token repository");
        }

        return match;
    }

    @Override
    public String toString()
    {
        return "VerifyTokenOperation{" + "repository=" + repository + '}';
    }
}
