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
import tech.aroma.banana.authentication.service.data.Token;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.authentication.service.GetUserTokenInfoRequest;
import tech.aroma.banana.thrift.authentication.service.GetUserTokenInfoResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.banana.authentication.service.AuthenticationAssertions.checkRequestNotNull;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@Internal
final class GetUserTokenInfoOperation implements ThriftOperation<GetUserTokenInfoRequest, GetUserTokenInfoResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(GetUserTokenInfoOperation.class);

    private final TokenRepository tokenRepository;

    @Inject
    GetUserTokenInfoOperation(TokenRepository tokenRepository)
    {
        checkThat(tokenRepository).is(notNull());

        this.tokenRepository = tokenRepository;
    }

    @Override
    public GetUserTokenInfoResponse process(GetUserTokenInfoRequest request) throws TException
    {
        LOG.debug("Received request to get token info: {}", request);

        checkRequestNotNull(request);
        checkThat(request.tokenId)
            .throwing(InvalidArgumentException.class)
            .is(nonEmptyString());

        Token token = tryGetToken(request.tokenId);

        return new GetUserTokenInfoResponse().setToken(token.asUserToken());
    }

    @Override
    public String toString()
    {
        return "GetUserTokenInfoOperation{" + "tokenRepository=" + tokenRepository + '}';
    }

    private Token tryGetToken(String tokenId) throws TException
    {
        Token token;
        try
        {
            token = tokenRepository.getToken(tokenId);
        }
        catch (TException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            LOG.error("Failed to retrieve Token from repository", ex);
            throw new OperationFailedException("Could not retrieve repository");
        }

        checkThat(token)
            .throwing(OperationFailedException.class)
            .is(notNull());

        return token;
    }

}
