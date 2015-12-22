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
import tech.sirwellington.alchemy.annotations.concurrency.Mutable;
import tech.sirwellington.alchemy.annotations.objects.Pojo;

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

    @Override
    public String toString()
    {
        return "Token{" + "tokenId=" + tokenId + ", timeOfCreation=" + timeOfCreation + ", timeOfExpiration=" + timeOfExpiration + ", ownerId=" + ownerId + '}';
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.tokenId);
        hash = 71 * hash + Objects.hashCode(this.timeOfCreation);
        hash = 71 * hash + Objects.hashCode(this.timeOfExpiration);
        hash = 71 * hash + Objects.hashCode(this.ownerId);
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
        return true;
    }

}
