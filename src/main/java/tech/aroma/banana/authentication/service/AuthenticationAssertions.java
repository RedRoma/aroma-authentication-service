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

 
package tech.aroma.banana.authentication.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;
import tech.sirwellington.alchemy.arguments.AlchemyAssertion;
import tech.sirwellington.alchemy.arguments.FailedAssertionException;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 *
 * @author SirWellington
 */
@Internal
@NonInstantiable
public final class AuthenticationAssertions 
{
    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationAssertions.class);
    
    private AuthenticationAssertions() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }
    
    public static void checkRequestNotNull(Object request) throws InvalidArgumentException
    {
        checkNotNull(request, "missing request");
    }
    
    public static void checkNotNull(Object reference, String message) throws InvalidArgumentException
    {
        checkThat(reference)
            .throwing(ex -> new InvalidArgumentException(message))
            .is(notNull());
    }
    
    public static AlchemyAssertion<String> tokenInRepository(@NonNull TokenRepository repository) throws IllegalArgumentException
    {
        checkThat(repository)
            .usingMessage("repository missing")
            .is(notNull());
        
        return token ->
        {
            boolean exists;
            try
            {
                exists = repository.tokenExists(token);
            }
            catch (OperationFailedException ex)
            {
                throw new FailedAssertionException("Could not check in repository", ex);
            }
            
            if (!exists)
            {
                throw new FailedAssertionException("Token does not exist: " + token);
            }
        };
    }

}
