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

 
package tech.aroma.banana.authentication.service;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.authentication.service.data.TokenCreator;
import tech.aroma.banana.thrift.authentication.service.AuthenticationService;

/**
 *
 * @author SirWellington
 */
public final class AuthenticationServiceModule extends AbstractModule
{
    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationServiceModule.class);

    @Override
    protected void configure()
    {
        bind(AuthenticationService.Iface.class).to(AuthenticationServiceImpl.class).in(Singleton.class);
    }
    
    @Singleton
    @Provides
    TokenCreator provideTokenCreator()
    {
        return TokenCreator.UUID;
    }

}
