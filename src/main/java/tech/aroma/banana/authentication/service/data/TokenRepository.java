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

import java.util.List;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;


/**
 * This interface is responsible for the storage and retrieval of 
 * Tokens.
 * 
 * @author SirWellington
 */
public interface TokenRepository 
{
    boolean tokenExists(@NonEmpty String tokenId) throws IllegalArgumentException;
    
    boolean tokenBelongsTo(@NonEmpty String tokenId, @NonEmpty String ownerId) throws IllegalArgumentException;
    
    Token getToken(@NonEmpty String tokenId) throws IllegalArgumentException, OperationFailedException;
    
    void saveToken(@NonNull Token token) throws IllegalArgumentException, OperationFailedException;
    
    List<Token> getTokensBelongingTo(String ownerId) throws IllegalArgumentException, OperationFailedException;
    
    void deleteToken(@NonEmpty String tokenId) throws IllegalArgumentException;
    
    default void deleteTokens(@NonNull List<String> tokenIds) throws IllegalArgumentException
    {
        checkThat(tokenIds).is(notNull());
        
        for (String token : tokenIds)
        {
            deleteToken(token);
        }
    }
    
}
