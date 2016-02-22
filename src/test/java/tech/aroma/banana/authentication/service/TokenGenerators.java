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
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.generator.AlchemyGenerator;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.ObjectGenerators.pojos;
import static tech.sirwellington.alchemy.generator.StringGenerators.uuids;

/**
 *
 * @author SirWellington
 */
@Internal
public final class TokenGenerators 
{
    private final static Logger LOG = LoggerFactory.getLogger(TokenGenerators.class);

    
    public static AlchemyGenerator<AuthenticationToken> authenticationTokens()
    {
        return () ->
        {
            AuthenticationToken token = one(pojos(AuthenticationToken.class));
            String ownerId = one(uuids);
            token.setOwnerId(ownerId);
            
            return token;
        };
    }
}
