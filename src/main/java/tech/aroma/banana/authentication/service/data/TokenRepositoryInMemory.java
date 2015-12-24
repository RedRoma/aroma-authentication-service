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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.thrift.exceptions.InvalidTokenException;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static java.time.Instant.now;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.tokenInRepository;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@StrategyPattern(role = CONCRETE_BEHAVIOR)
final class TokenRepositoryInMemory implements TokenRepository
{

    private final static Logger LOG = LoggerFactory.getLogger(TokenRepositoryInMemory.class);

    private final Map<String, Token> tokens = Maps.newConcurrentMap();
    private final Map<String, List<Token>> tokensByOwner = Maps.newConcurrentMap();
    private final Map<String, Instant> tokenExpiration = Maps.newConcurrentMap();

    @Override
    public boolean doesTokenExist(String tokenId) throws IllegalArgumentException
    {
        checkThat(tokenId).is(nonEmptyString());

        if (!tokens.containsKey(tokenId))
        {
            return false;
        }

        if (isExpired(tokenId))
        {
            LOG.debug("Token is now expired. Removing it. {}", tokenId);
            removeToken(tokenId);
            return false;
        }
        
        return true;
    }


    @Override
    public Token getToken(String tokenId) throws TException
    {
        checkThat(tokenId)
            .is(nonEmptyString())
            .throwing(InvalidTokenException.class)
            .is(tokenInRepository(this));

        if (isExpired(tokenId))
        {
            removeToken(tokenId);
        }

        return tokens.get(tokenId);
    }

    @Override
    public void saveToken(Token token) throws TException
    {
        checkThat(token)
            .usingMessage("token is null")
            .is(notNull());
        
        checkThat(token.getTokenType())
            .usingMessage("tokenType is required")
            .is(notNull());
        
        Instant expiration = token.getTimeOfExpiration();
        checkThat(expiration)
            .usingMessage("token is missing an expiration date.")
            .is(notNull());
        
        String tokenId = token.getTokenId();
        String ownerId = token.getOwnerId();
        
        checkThat(tokenId, ownerId)
            .usingMessage("tokenId and ownerId must be present in Token")
            .are(nonEmptyString());
            
        tokens.put(tokenId, token);
        tokenExpiration.put(tokenId, expiration);
        
        List<Token> ownerTokens = tokensByOwner.getOrDefault(ownerId, Lists.newArrayList());
        ownerTokens.add(token);
        tokensByOwner.put(ownerId, ownerTokens);
    }

    @Override
    public List<Token> getTokensBelongingTo(String ownerId) throws IllegalArgumentException
    {
        checkThat(ownerId).is(nonEmptyString());
        
        List<Token> allTokens = tokensByOwner.getOrDefault(ownerId, Lists.newArrayList());
        
        //Sending a separate list ensures we send a defensive copy.
        List<Token> results = Lists.newArrayList();
        
        for (Token token : allTokens)
        {
            String tokenId = token.getTokenId();
            if (isExpired(tokenId))
            {
                removeToken(tokenId);
                continue;
            }
            results.add(token);
        }
        
        return results;
    }

    @Override
    public void deleteToken(String tokenId) throws IllegalArgumentException
    {
        checkThat(tokenId).is(nonEmptyString());
        
        removeToken(tokenId);
    }

    private boolean isExpired(String tokenId)
    {
        Instant now = now();
        Instant expiration = tokenExpiration.get(tokenId);

        if (expiration == null)
        {
            return false;
        }
        
        return expiration.isBefore(now);
    }

    private void removeToken(String tokenId)
    {
        tokenExpiration.remove(tokenId);
        Token token = tokens.remove(tokenId);
        
        //Token never existed
        if (token == null)
        {
            return;
        }
        
        String ownerId = token.getOwnerId();
        
        List<Token> tokens = tokensByOwner.get(ownerId);
        
        if (tokens != null)
        {
            tokens.remove(token);
        }
    }

}
