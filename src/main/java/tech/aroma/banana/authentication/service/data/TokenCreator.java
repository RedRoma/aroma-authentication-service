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

import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.StringGenerators.hexadecimalString;

/**
 * This interface is responsible for creating a globally unique token.
 *
 * @author SirWellington
 */
@Internal
@FunctionalInterface
@StrategyPattern(role = INTERFACE)
public interface TokenCreator
{

    /**
     * Creates a String expected to be unique accross calls and accross machines.
     * @return 
     */
    String create();

    @StrategyPattern(role = CONCRETE_BEHAVIOR)
    TokenCreator UUID = () -> java.util.UUID.randomUUID().toString();

    @StrategyPattern(role = CONCRETE_BEHAVIOR)
    TokenCreator UUID_PLUS_HEX = () -> UUID.create() + one(hexadecimalString(2000));

}
