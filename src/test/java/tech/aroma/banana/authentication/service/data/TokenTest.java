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

package tech.aroma.banana.authentication.service.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tech.aroma.banana.thrift.authentication.ApplicationToken;
import tech.aroma.banana.thrift.authentication.UserToken;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * @author SirWellington
 */
@Repeat(50)
@RunWith(AlchemyTestRunner.class)
public class TokenTest 
{

    @GeneratePojo
    private Token token;
    
    @Before
    public void setUp()
    {
    }

    @Test
    public void testAsApplicationToken()
    {
        ApplicationToken appToken = token.asApplicationToken();
        assertThat(appToken, notNullValue());
        assertThat(appToken.applicationId, is(token.getOwnerId()));
        assertThat(appToken.tokenId, is(token.getTokenId()));
        assertThat(appToken.timeOfExpiration, is(token.getTimeOfExpiration().toEpochMilli()));
    }

    @Test
    public void testAsUserToken()
    {
        UserToken userToken = token.asUserToken();
        assertThat(userToken, notNullValue());
        assertThat(userToken.tokenId, is(token.getTokenId()));
        assertThat(userToken.getTimeOfExpiration(), is(token.getTimeOfExpiration().toEpochMilli()));
    }

}