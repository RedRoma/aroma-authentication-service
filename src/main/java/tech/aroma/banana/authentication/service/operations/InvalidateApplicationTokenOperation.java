
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
import tech.aroma.banana.thrift.authentication.service.InvalidateApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateApplicationTokenResponse;
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
final class InvalidateApplicationTokenOperation implements ThriftOperation<InvalidateApplicationTokenRequest, InvalidateApplicationTokenResponse>
{

    private final static Logger LOG = LoggerFactory.getLogger(InvalidateApplicationTokenOperation.class);

    private final TokenRepository repository;

    @Inject
    InvalidateApplicationTokenOperation(TokenRepository repository)
    {
        checkThat(repository).is(notNull());
        
        this.repository = repository;
    }

    @Override
    public InvalidateApplicationTokenResponse process(InvalidateApplicationTokenRequest request) throws TException
    {
        LOG.debug("Received request to invalidate token: {}", request);

        checkRequestNotNull(request);
        checkNotNull(request.token, "request is missing token");
        checkThat(request.token.tokenId)
            .throwing(InvalidArgumentException.class)
            .is(nonEmptyString());
        String tokenId = request.token.tokenId;
        
        tryDelete(tokenId);
        
        return new InvalidateApplicationTokenResponse();
    }

    private void tryDelete(String tokenId) throws TException
    {
        try
        {
            repository.deleteToken(tokenId);
        }
        catch(TException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            throw new OperationFailedException("Could not delete token");
        }
    }

    @Override
    public String toString()
    {
        return "InvalidateApplicationTokenOperation{" + "repository=" + repository + '}';
    }
    
}
