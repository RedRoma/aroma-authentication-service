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


import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.authentication.service.AuthenticationAssertions;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.LengthOfTime;
import tech.aroma.banana.thrift.TimeUnit;
import tech.aroma.banana.thrift.authentication.service.CreateApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateApplicationTokenResponse;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.sirwellington.alchemy.generator.ObjectGenerators.pojos;

/**
 *
 * @author SirWellington
 */
@Internal
final class CreateApplicationTokenOperation implements ThriftOperation<CreateApplicationTokenRequest, CreateApplicationTokenResponse>
{
    private final static Logger LOG = LoggerFactory.getLogger(CreateApplicationTokenOperation.class);
    private final static LengthOfTime DEFAULT_LIFETIME = new LengthOfTime(TimeUnit.DAYS, 30);

    private TokenCreator tokenCreator;
    private TokenRepository tokenRepository;

    @Override
    public CreateApplicationTokenResponse process(CreateApplicationTokenRequest request) throws TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        LOG.debug("Received request to create an Application Token: {}", request);
        
        //Check the request
        //Create the token
        //Store the token
        //Return the token
        
        CreateApplicationTokenResponse response = pojos(CreateApplicationTokenResponse.class).get();
        
        return response;
    }

}
