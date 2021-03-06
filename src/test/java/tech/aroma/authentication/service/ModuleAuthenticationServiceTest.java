/*
 * Copyright 2017 RedRoma, Inc.
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

package tech.aroma.authentication.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tech.aroma.authentication.service.data.TokenCreator;
import tech.aroma.authentication.service.operations.ModuleAuthenticationOperations;
import tech.aroma.data.memory.ModuleMemoryDataRepositories;
import tech.aroma.thrift.authentication.service.AuthenticationService;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class ModuleAuthenticationServiceTest
{

    private ModuleMemoryDataRepositories dataModule;
    
    private ModuleAuthenticationOperations operationsModule;

    private ModuleAuthenticationService instance;

    @Before
    public void setUp()
    {
        dataModule = new ModuleMemoryDataRepositories();
        operationsModule = new ModuleAuthenticationOperations();
        instance = new ModuleAuthenticationService();
    }

    @Test
    public void testConfigure()
    {
        Injector injector = Guice.createInjector(dataModule, 
                                                 operationsModule, 
                                                 instance);
        
        AuthenticationService.Iface service = injector.getInstance(AuthenticationService.Iface.class);
        assertThat(service, notNullValue());
    }

    @Test
    public void testProvideTokenCreator()
    {
        TokenCreator tokenCreator = instance.provideTokenCreator();
        assertThat(tokenCreator, notNullValue());
    }

}
