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

package tech.aroma.banana.authentication.service.operations;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.data.memory.ModuleMemoryDataRepositories;
import tech.sirwellington.alchemy.annotations.testing.IntegrationTest;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;

import static org.mockito.Mockito.mock;

/**
 *
 * @author SirWellington
 */
@IntegrationTest
@RunWith(AlchemyTestRunner.class)
public class AuthenticationOperationsModuleTest 
{
    private ModuleMemoryDataRepositories dataModule;

    private AuthenticationOperationsModule module;
    
    @Before
    public void setUp()
    {
        dataModule = new ModuleMemoryDataRepositories();
        module = new AuthenticationOperationsModule();
    }

    @Test
    public void testConfigure()
    {
        Injector injector = Guice.createInjector(dataModule,
                                                 module,
                                                 tokenCreatorModule);
    }
    
    private Module tokenCreatorModule = new AbstractModule()
    {
        @Override
        protected void configure()
        {
        }
        
        @Provides
        TokenCreator provideTokenCreator()
        {
            return mock(TokenCreator.class);
        }
    };

}