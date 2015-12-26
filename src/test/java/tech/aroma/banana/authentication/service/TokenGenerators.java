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
import tech.aroma.banana.thrift.authentication.ApplicationToken;
import tech.aroma.banana.thrift.authentication.AuthenticationToken;
import tech.aroma.banana.thrift.authentication.UserToken;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.generator.AlchemyGenerator;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.smallPositiveIntegers;
import static tech.sirwellington.alchemy.generator.ObjectGenerators.pojos;

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
            AuthenticationToken token = new AuthenticationToken();
            
            int branch = one(smallPositiveIntegers());
            
            if (branch % 2 == 0)
            {
                ApplicationToken appToken = one(pojos(ApplicationToken.class));
                token.setApplicationToken(appToken);
            }
            else
            {
                UserToken userToken = one(pojos(UserToken.class));
                token.setUserToken(userToken);
            }
            
            return token;
        };
    }
}
