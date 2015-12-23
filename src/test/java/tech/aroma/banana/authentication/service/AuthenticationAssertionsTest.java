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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.aroma.banana.authentication.service.data.TokenRepository;
import tech.aroma.banana.thrift.authentication.ApplicationToken;
import tech.aroma.banana.thrift.authentication.UserToken;
import tech.aroma.banana.thrift.authentication.service.AuthenticationToken;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.sirwellington.alchemy.arguments.AlchemyAssertion;
import tech.sirwellington.alchemy.arguments.ExceptionMapper;
import tech.sirwellington.alchemy.arguments.FailedAssertionException;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.DontRepeat;
import tech.sirwellington.alchemy.test.junit.runners.GenerateString;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.ObjectGenerators.pojos;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticString;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@Repeat(50)
@RunWith(AlchemyTestRunner.class)
public class AuthenticationAssertionsTest 
{

    @Mock
    private TokenRepository tokenRepository;
    
    @GenerateString
    private String token;
    
    @Before
    public void setUp()
    {
    }

    @DontRepeat
    @Test
    public void testCannotInstantiate() throws Exception
    {
        assertThrows(() -> AuthenticationAssertions.class.newInstance())
            .isInstanceOf(IllegalAccessException.class);
    }
    
    @Test
    public void testCheckRequestNotNull() throws Exception
    {
        
        assertThrows(() -> AuthenticationAssertions.checkRequestNotNull(null))
            .isInstanceOf(InvalidArgumentException.class);
        
        String string = one(strings());
        AuthenticationAssertions.checkRequestNotNull(string);
    }

    @Test
    public void testTokenInRepository() throws Exception
    {
        AlchemyAssertion<String> instance = AuthenticationAssertions.tokenInRepository(tokenRepository);
        assertThat(instance, notNullValue());

        when(tokenRepository.tokenExists(token))
            .thenReturn(true);
        instance.check(token);

        when(tokenRepository.tokenExists(token))
            .thenReturn(false);
        assertThrows(() -> instance.check(token));

    }

    @Test
    public void testCheckNotNull() throws Exception
    {
        String message = one(alphabeticString());
        AuthenticationAssertions.checkNotNull(token, message);
        
        assertThrows(() -> AuthenticationAssertions.checkNotNull(null, message))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessage(message);
    }

    @Test
    public void testWithMessage()
    {
        
        String message = one(strings());
        ExceptionMapper<InvalidArgumentException> result = AuthenticationAssertions.withMessage(message);
        
        InvalidArgumentException ex = result.apply(null);
        assertThat(ex, notNullValue());
        assertThat(ex.getMessage(), is(message));
    }

    @Test
    public void testLegalToken()
    {
        AlchemyAssertion<AuthenticationToken> instance = AuthenticationAssertions.legalToken();
        assertThat(instance, notNullValue());

        AuthenticationToken authenticationToken = new AuthenticationToken();

        assertThrows(() -> instance.check(authenticationToken))
            .isInstanceOf(FailedAssertionException.class);

        ApplicationToken applicationToken = one(pojos(ApplicationToken.class));
        authenticationToken.setApplicationToken(applicationToken);
        instance.check(authenticationToken);
        
        UserToken userToken = one(pojos(UserToken.class));
        authenticationToken.setUserToken(userToken);
        instance.check(authenticationToken);
        
    }

}