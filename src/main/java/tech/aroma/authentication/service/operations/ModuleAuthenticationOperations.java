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

 
package tech.aroma.authentication.service.operations;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import java.time.Duration;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.thrift.LengthOfTime;
import tech.aroma.thrift.authentication.service.CreateTokenRequest;
import tech.aroma.thrift.authentication.service.CreateTokenResponse;
import tech.aroma.thrift.authentication.service.GetTokenInfoRequest;
import tech.aroma.thrift.authentication.service.GetTokenInfoResponse;
import tech.aroma.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.thrift.authentication.service.VerifyTokenRequest;
import tech.aroma.thrift.authentication.service.VerifyTokenResponse;
import tech.aroma.thrift.functions.TimeFunctions;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

/**
 *
 * @author SirWellington
 */
public final class ModuleAuthenticationOperations extends AbstractModule
{
    private final static Logger LOG = LoggerFactory.getLogger(ModuleAuthenticationOperations.class);

    @Override
    protected void configure()
    {
        bind(new TypeLiteral<ThriftOperation<CreateTokenRequest, CreateTokenResponse>>() {})
            .to(CreateTokenOperation.class);
        
        bind(new TypeLiteral<ThriftOperation<GetTokenInfoRequest, GetTokenInfoResponse>>() {})
            .to(GetTokenInfoOperation.class);
        
        
        bind(new TypeLiteral<ThriftOperation<InvalidateTokenRequest, InvalidateTokenResponse>>() {})
            .to(InvalidateTokenOperation.class);
        
        bind(new TypeLiteral<ThriftOperation<VerifyTokenRequest, VerifyTokenResponse>>() {})
            .to(VerifyTokenOperation.class);
        
    }
    
    @Provides
    Function<LengthOfTime, Duration> provideLengthOfTimeConverter()
    {
        return TimeFunctions.LENGTH_OF_TIME_TO_DURATION;
    }
    
}
