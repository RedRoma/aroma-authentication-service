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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.thrift.authentication.service.CreateApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateApplicationTokenResponse;
import tech.aroma.banana.thrift.authentication.service.CreateUserTokenRequest;
import tech.aroma.banana.thrift.authentication.service.CreateUserTokenResponse;
import tech.aroma.banana.thrift.authentication.service.GetApplicationTokenInfoRequest;
import tech.aroma.banana.thrift.authentication.service.GetApplicationTokenInfoResponse;
import tech.aroma.banana.thrift.authentication.service.GetUserTokenInfoRequest;
import tech.aroma.banana.thrift.authentication.service.GetUserTokenInfoResponse;
import tech.aroma.banana.thrift.authentication.service.InvalidateApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateApplicationTokenResponse;
import tech.aroma.banana.thrift.authentication.service.InvalidateUserTokenRequest;
import tech.aroma.banana.thrift.authentication.service.InvalidateUserTokenResponse;
import tech.aroma.banana.thrift.authentication.service.VerifyApplicationTokenRequest;
import tech.aroma.banana.thrift.authentication.service.VerifyApplicationTokenResponse;
import tech.aroma.banana.thrift.authentication.service.VerifyUserTokenRequest;
import tech.aroma.banana.thrift.authentication.service.VerifyUserTokenResponse;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

/**
 *
 * @author SirWellington
 */
public final class AuthenticationOperationsModule extends AbstractModule
{
    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationOperationsModule.class);

    @Override
    protected void configure()
    {
        
    }
    
    
    
    private ThriftOperation<CreateApplicationTokenRequest, CreateApplicationTokenResponse> createApplicationTokenOperation;
    private ThriftOperation<CreateUserTokenRequest, CreateUserTokenResponse> createUserTokenOperation;
    private ThriftOperation<GetApplicationTokenInfoRequest, GetApplicationTokenInfoResponse> getApplicationTokenInfoOperation;
    private ThriftOperation<GetUserTokenInfoRequest, GetUserTokenInfoResponse> getUserTokenInfoOperation;
    private ThriftOperation<InvalidateApplicationTokenRequest, InvalidateApplicationTokenResponse> invalidateApplicationTokenOperation;
    private ThriftOperation<InvalidateUserTokenRequest, InvalidateUserTokenResponse> invalidateUserTokenOperation;
    private ThriftOperation<VerifyApplicationTokenRequest, VerifyApplicationTokenResponse> verifyApplicationTokenOperation;
    private ThriftOperation<VerifyUserTokenRequest, VerifyUserTokenResponse> verifyUserTokenOperation;
    
}