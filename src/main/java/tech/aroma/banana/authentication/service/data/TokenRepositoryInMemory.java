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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static java.time.Instant.now;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.tokenInRepository;
import static tech.aroma.banana.authentication.service.AuthenticationAssertions.withMessage;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;
import static java.time.Instant.now;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static java.time.Instant.now;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static java.time.Instant.now;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;

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
    public boolean doesTokenExist(String tokenId) throws InvalidArgumentException
    {

        if (!tokens.containsKey(tokenId))
        {
            return false;
        }

        if (isExpired(tokenId))
        {
            removeToken(tokenId);
        }
        
        return true;
    }

    @Override
    public boolean doesTokenBelongTo(String tokenId, String ownerId) throws InvalidArgumentException
    {
        checkThat(tokenId, ownerId)
            .are(nonEmptyString());
        
        checkThat(tokenId)
            .is(tokenInRepository(this));
        
        Token token = tokens.get(tokenId);
        
        return ownerId.equals(token.getOwnerId());
    }

    @Override
    public Token getToken(String tokenId) throws InvalidArgumentException, OperationFailedException
    {
        checkThat(tokenId)
            .is(nonEmptyString())
            .is(tokenInRepository(this));

        if (isExpired(tokenId))
        {
            removeToken(tokenId);
        }

        return tokens.get(tokenId);
    }

    @Override
    public void saveToken(Token token) throws InvalidArgumentException, OperationFailedException
    {
        checkThat(token).is(notNull());
        
        Instant expiration = token.getTimeOfExpiration();
        checkThat(expiration)
            .throwing(withMessage("token is missing an expiration date."))
            .is(notNull());
        
        String tokenId = token.getTokenId();
        String ownerId = token.getOwnerId();
        
        checkThat(tokenId, ownerId)
            .throwing(withMessage("tokenId and ownerId must be present in Token"))
            .are(nonEmptyString());
            
        tokens.put(tokenId, token);
        tokenExpiration.put(tokenId, expiration);
        
        List<Token> ownerTokens = tokensByOwner.getOrDefault(ownerId, Lists.newArrayList());
        ownerTokens.add(token);
    }

    @Override
    public List<Token> getTokensBelongingTo(String ownerId) throws InvalidArgumentException, OperationFailedException
    {
        checkThat(ownerId).is(nonEmptyString());
        
        return tokensByOwner.getOrDefault(ownerId, Lists.newArrayList());
    }

    @Override
    public void deleteToken(String tokenId) throws InvalidArgumentException
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
        String ownerId = token.getOwnerId();
        
        List<Token> tokens = tokensByOwner.get(ownerId);
        
        if (tokens != null)
        {
            tokens.remove(token);
        }
    }

}
