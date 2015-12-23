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
import tech.aroma.banana.thrift.authentication.service.GetApplicationTokenInfoRequest;
import tech.aroma.banana.thrift.authentication.service.GetApplicationTokenInfoResponse;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.InvalidTokenException;
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
final class GetApplicationTokenInfoOperation implements ThriftOperation<GetApplicationTokenInfoRequest, GetApplicationTokenInfoResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(GetApplicationTokenInfoOperation.class);

    private final TokenRepository tokenRepository;

    @Inject
    GetApplicationTokenInfoOperation(TokenRepository tokenRepository)
    {
        checkThat(tokenRepository).is(notNull());
        
        this.tokenRepository = tokenRepository;
    }

    @Override
    public GetApplicationTokenInfoResponse process(GetApplicationTokenInfoRequest request) throws TException
    {
        LOG.debug("Received request to get token info: {}", request);

        checkRequestNotNull(request);
        
        String tokenId = request.tokenId;
        
        checkThat(tokenId)
            .throwing(ex -> new InvalidArgumentException("tokenId and applicationid are required"))
            .is(nonEmptyString());
        
        Token token = tokenRepository.getToken(tokenId);
        
        checkThat(token)
            .throwing(InvalidTokenException.class)
            .is(notNull());
        
        return new GetApplicationTokenInfoResponse()
            .setToken(token.asApplicationToken())
            ;
    }

}
