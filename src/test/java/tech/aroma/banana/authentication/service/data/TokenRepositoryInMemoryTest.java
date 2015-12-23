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

import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tech.aroma.banana.thrift.exceptions.InvalidTokenException;
import tech.sirwellington.alchemy.generator.AlchemyGenerator;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.DontRepeat;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static java.time.Instant.now;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.CollectionGenerators.listOf;
import static tech.sirwellington.alchemy.generator.ObjectGenerators.pojos;
import static tech.sirwellington.alchemy.generator.StringGenerators.hexadecimalString;
import static tech.sirwellington.alchemy.generator.TimeGenerators.futureInstants;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@Repeat(100)
@RunWith(AlchemyTestRunner.class)
public class TokenRepositoryInMemoryTest
{

    private TokenRepository repository;

    @GeneratePojo
    private Token token;

    private List<Token> tokens;

    private String tokenId;
    private String ownerId;

    @Before
    public void setUp()
    {
        repository = new TokenRepositoryInMemory();

        tokenId = token.getTokenId();
        ownerId = token.getOwnerId();

        AlchemyGenerator<Token> tokenGenerator = pojos(Token.class);
        tokens = listOf(tokenGenerator, 10);
        tokens.forEach(t -> t.setOwnerId(ownerId));

        Instant timeOfExpiration = one(futureInstants());
        token.setTimeOfExpiration(timeOfExpiration);
        tokens.forEach(t -> t.setTimeOfExpiration(timeOfExpiration));
    }

    @Test
    public void testDoesTokenExist() throws Exception
    {
        assertThat(repository.doesTokenExist(tokenId), is(false));

        repository.saveToken(token);
        assertThat(repository.doesTokenExist(tokenId), is(true));
    }

    @DontRepeat
    @Test
    public void testDoesTokenExistWithBadArgs() throws Exception
    {
        assertThrows(() -> repository.doesTokenExist(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testDoesTokenExistWhenTokenExpired() throws Exception
    {
        token.setTimeOfExpiration(now().minusSeconds(3));
        repository.saveToken(token);

        assertThat(repository.doesTokenExist(tokenId), is(false));
    }

    @Test
    public void testGetToken() throws Exception
    {
        assertThrows(() -> repository.getToken(tokenId))
            .isInstanceOf(InvalidTokenException.class);

        repository.saveToken(token);

        Token result = repository.getToken(tokenId);
        assertThat(result, is(token));
    }

    @Test
    public void testGetTokenWithBadArgs() throws Exception
    {
        assertThrows(() -> repository.getToken(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testGetTokenWhenTokenExpired() throws Exception
    {
        token.setTimeOfExpiration(now().minusSeconds(5));
        repository.saveToken(token);

        assertThrows(() -> repository.getToken(tokenId))
            .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    public void testSaveToken() throws Exception
    {
        repository.saveToken(token);

        Token result = repository.getToken(tokenId);
        assertThat(result, is(token));
    }

    @Test
    public void testSaveTokenWithBadArgs() throws Exception
    {
        assertThrows(() -> repository.saveToken(null))
            .isInstanceOf(IllegalArgumentException.class);

        //Missing Owner ID
        token.setOwnerId("");

        assertThrows(() -> repository.saveToken(token))
            .isInstanceOf(IllegalArgumentException.class);

        //Missing Token ID
        token.setOwnerId(ownerId);
        token.setTokenId("");

        assertThrows(() -> repository.saveToken(token))
            .isInstanceOf(IllegalArgumentException.class);

        //Missing Token Type
        token.setTokenId(tokenId);
        token.setTokenType(null);

        assertThrows(() -> repository.saveToken(token))
            .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void testGetTokensBelongingTo() throws Exception
    {
        for (Token token : tokens)
        {
            repository.saveToken(token);
        }

        List<Token> result = repository.getTokensBelongingTo(ownerId);
        Set<Token> expected = Sets.newHashSet(tokens);
        Set<Token> resultSet = Sets.newHashSet(result);
        assertThat(resultSet, is(expected));
    }

    @Test
    public void testGetTokensWhenNoneExistForOwner() throws Exception
    {
        String ownerId = one(hexadecimalString(10));
        List<Token> result = repository.getTokensBelongingTo(ownerId);
        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testGetTokensBelongingToWithBadArgs() throws Exception
    {
        assertThrows(() -> repository.getTokensBelongingTo(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testDeleteToken() throws Exception
    {
        repository.saveToken(token);
        assertThat(repository.doesTokenExist(tokenId), is(true));
        
        repository.deleteToken(tokenId);
        assertThat(repository.doesTokenExist(tokenId), is(false));
    }

    @Test
    public void testDeleteTokenWithBadArgs() throws Exception
    {
        assertThrows(() -> repository.deleteToken(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testDeleteTokenWhenTokenDoesNotExist() throws Exception
    {
        repository.deleteToken(tokenId);
    }

}
