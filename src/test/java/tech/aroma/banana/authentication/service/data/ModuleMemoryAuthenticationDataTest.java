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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class ModuleMemoryAuthenticationDataTest 
{

    private ModuleMemoryAuthenticationData module;
    
    @Before
    public void setUp()
    {
        module = new ModuleMemoryAuthenticationData();
    }

    @Test
    public void testConfigure()
    {
        Injector injector = Guice.createInjector(module);
    }

    @Test
    public void testProvideTokenCreator()
    {
        TokenCreator result = module.provideTokenCreator();
        assertThat(result, notNullValue());
    }

    @Test
    public void testProvideTokenRepository()
    {
        TokenRepository result = module.provideTokenRepository();
        assertThat(result, notNullValue());
    }

}