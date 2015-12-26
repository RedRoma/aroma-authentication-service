
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
import tech.aroma.banana.thrift.authentication.AuthenticationToken;
import tech.aroma.banana.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.banana.authentication.service.AuthenticationAssertions.checkRequestNotNull;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.legalToken;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.withMessage;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@Internal
final class InvalidateTokenOperation implements ThriftOperation<InvalidateTokenRequest, InvalidateTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(InvalidateTokenOperation.class);

    private final TokenRepository repository;

    @Inject
    InvalidateTokenOperation(TokenRepository repository)
    {
        checkThat(repository).is(notNull());
        
        this.repository = repository;
    }

    @Override
    public InvalidateTokenResponse process(InvalidateTokenRequest request) throws TException
    {
        LOG.debug("Received request to invalidate token: {}", request);

        checkRequestNotNull(request);
        checkThat(request.token)
            .throwing(withMessage("request is missing token"))
            .is(notNull())
            .is(legalToken());
        
        String tokenId = extractTokenId(request.token);
        checkThat(tokenId)
            .throwing(withMessage("missing tokenId"))
            .is(nonEmptyString());
        
        tryDelete(tokenId);
        
        return new InvalidateTokenResponse();
    }

    private String extractTokenId(AuthenticationToken token)
    {
        if(token.isSetApplicationToken())
        {
            return token.getApplicationToken().tokenId;
        }
        else
        {
            return token.getUserToken().tokenId;
        }
    }
    
    private void tryDelete(String tokenId) throws TException
    {
        try
        {
            repository.deleteToken(tokenId);
        }
        catch(Exception ex)
        {
            throw new OperationFailedException("Could not delete token");
        }
    }

    @Override
    public String toString()
    {
        return "InvalidateTokenOperation{" + "repository=" + repository + '}';
    }
    
}
