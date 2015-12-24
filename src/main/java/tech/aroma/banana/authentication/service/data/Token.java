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

import java.time.Instant;
import java.util.Objects;
import tech.aroma.banana.thrift.authentication.ApplicationToken;
import tech.aroma.banana.thrift.authentication.UserToken;
import tech.aroma.banana.thrift.authentication.service.AuthenticationToken;
import tech.aroma.banana.thrift.authentication.service.TokenType;
import tech.sirwellington.alchemy.annotations.concurrency.Mutable;
import tech.sirwellington.alchemy.annotations.objects.Pojo;

import static tech.aroma.banana.thrift.authentication.service.TokenType.APPLICATION;
import static tech.aroma.banana.thrift.authentication.service.TokenType.USER;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 *
 * @author SirWellington
 */
@Mutable
@Pojo
public final class Token
{

    private String tokenId;
    private Instant timeOfCreation;
    private Instant timeOfExpiration;
    private String ownerId;
    private TokenType tokenType;

    public Token()
    {
    }

    public String getTokenId()
    {
        return tokenId;
    }

    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public Instant getTimeOfCreation()
    {
        return timeOfCreation;
    }

    public void setTimeOfCreation(Instant timeOfCreation)
    {
        this.timeOfCreation = timeOfCreation;
    }

    public Instant getTimeOfExpiration()
    {
        return timeOfExpiration;
    }

    public void setTimeOfExpiration(Instant timeOfExpiration)
    {
        this.timeOfExpiration = timeOfExpiration;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public TokenType getTokenType()
    {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType)
    {
        this.tokenType = tokenType;
    }

    @Override
    public String toString()
    {
        return "Token{" + "tokenId=" + tokenId + ", timeOfCreation=" + timeOfCreation + ", timeOfExpiration=" + timeOfExpiration + ", ownerId=" + ownerId + ", tokenType=" + tokenType + '}';
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.tokenId);
        hash = 59 * hash + Objects.hashCode(this.timeOfCreation);
        hash = 59 * hash + Objects.hashCode(this.timeOfExpiration);
        hash = 59 * hash + Objects.hashCode(this.ownerId);
        hash = 59 * hash + Objects.hashCode(this.tokenType);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Token other = (Token) obj;
        if (!Objects.equals(this.tokenId, other.tokenId))
        {
            return false;
        }
        if (!Objects.equals(this.ownerId, other.ownerId))
        {
            return false;
        }
        if (!Objects.equals(this.timeOfCreation, other.timeOfCreation))
        {
            return false;
        }
        if (!Objects.equals(this.timeOfExpiration, other.timeOfExpiration))
        {
            return false;
        }
        if (this.tokenType != other.tokenType)
        {
            return false;
        }
        return true;
    }

    public AuthenticationToken asAuthenticationToken()
    {
        checkThat(tokenType)
            .usingMessage("missing Token Type")
            .is(notNull());

        AuthenticationToken token = new AuthenticationToken();
        if (tokenType == APPLICATION)
        {
            token.setApplicationToken(asApplicationToken());
        }
        else if (tokenType == USER)
        {
            token.setUserToken(asUserToken());
        }

        return token;
    }
    
    public ApplicationToken asApplicationToken()
    {
        ApplicationToken token = new ApplicationToken();
        token.setTokenId(tokenId)
            .setApplicationId(ownerId);

        if (timeOfExpiration != null)
        {
            token.setTimeOfExpiration(timeOfExpiration.toEpochMilli());
        }

        return token;
    }

    public UserToken asUserToken()
    {
        UserToken token = new UserToken()
            .setTokenId(tokenId);

        if (timeOfExpiration != null)
        {
            token.setTimeOfExpiration(timeOfExpiration.toEpochMilli());
        }

        return token;
    }

}
