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
import tech.aroma.banana.thrift.exceptions.InvalidArgumentException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;


/**
 * This interface is responsible for the storage and retrieval of 
 * Tokens.
 * 
 * @author SirWellington
 */
@StrategyPattern(role = INTERFACE)
public interface TokenRepository 
{

    boolean doesTokenExist(@NonEmpty String tokenId) throws InvalidArgumentException, OperationFailedException;

    boolean doesTokenBelongTo(@NonEmpty String tokenId, @NonEmpty String ownerId) throws InvalidArgumentException,
                                                                                      OperationFailedException;

    Token getToken(@NonEmpty String tokenId) throws InvalidArgumentException, OperationFailedException;

    void saveToken(@NonNull Token token) throws InvalidArgumentException, OperationFailedException;

    List<Token> getTokensBelongingTo(String ownerId) throws InvalidArgumentException, OperationFailedException;

    void deleteToken(@NonEmpty String tokenId) throws InvalidArgumentException, OperationFailedException;

    default void deleteTokens(@NonNull List<String> tokenIds) throws InvalidArgumentException, OperationFailedException
    {
        checkThat(tokenIds).is(notNull());

        for (String token : tokenIds)
        {
            deleteToken(token);
        }
    }

}
