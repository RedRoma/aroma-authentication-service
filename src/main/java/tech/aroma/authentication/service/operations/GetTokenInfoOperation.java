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

import javax.inject.Inject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.data.TokenRepository;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.TokenStatus;
import tech.aroma.thrift.authentication.service.GetTokenInfoRequest;
import tech.aroma.thrift.authentication.service.GetTokenInfoResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.aroma.thrift.functions.TimeFunctions;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.thrift.assertions.AromaAssertions.checkRequestNotNull;
import static tech.aroma.thrift.assertions.AromaAssertions.withMessage;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@Internal
final class GetTokenInfoOperation implements ThriftOperation<GetTokenInfoRequest, GetTokenInfoResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(GetTokenInfoOperation.class);

    private final TokenRepository tokenRepository;

    @Inject
    GetTokenInfoOperation(TokenRepository tokenRepository)
    {
        checkThat(tokenRepository).is(notNull());

        this.tokenRepository = tokenRepository;
    }

    @Override
    public GetTokenInfoResponse process(GetTokenInfoRequest request) throws TException
    {
        LOG.debug("Received request to get token info: {}", request);

        checkRequestNotNull(request);

        String tokenId = request.tokenId;

        checkThat(tokenId)
            .throwing(InvalidArgumentException.class)
            .usingMessage("tokenId is required")
            .is(nonEmptyString());

        checkThat(request.tokenType)
            .throwing(withMessage("token type is required"))
            .is(notNull());

        AuthenticationToken token = tryGetToken(tokenId);
        
        if (expirationDateHasPassed(token))
        {
            saveAsExpired(token);
        }
        
        return new GetTokenInfoResponse().setToken(token);
    }

    private AuthenticationToken tryGetToken(String tokenId) throws TException
    {
        AuthenticationToken token;

        try
        {
            token = tokenRepository.getToken(tokenId);
        }
        catch (Exception ex)
        {
            throw new OperationFailedException("Failed to load token from repository" + ex.getMessage());
        }

        checkThat(token)
            .throwing(OperationFailedException.class)
            .is(notNull());

        return token;
    }

    private boolean expirationDateHasPassed(AuthenticationToken token)
    {
        return TimeFunctions.isInThePast(token.timeOfExpiration);
    }

    private void saveAsExpired(AuthenticationToken token) throws TException
    {
        token.setStatus(TokenStatus.EXPIRED);
        tokenRepository.saveToken(token);
    }

}
