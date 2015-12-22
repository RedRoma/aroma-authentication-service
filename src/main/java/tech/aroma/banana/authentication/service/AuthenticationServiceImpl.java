
package tech.aroma.banana.authentication.service;

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


import javax.inject.Inject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.banana.thrift.authentication.service.AuthenticationService;
import tech.aroma.banana.thrift.authentication.service.AuthenticationServiceConstants;
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
import tech.aroma.banana.thrift.exceptions.InvalidTokenException;
import tech.aroma.banana.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 * This is the Implementation of the {@linkplain AuthenticationService.Iface Banana Authentication Service.}
 * @author SirWellington
 */
@Internal
final class AuthenticationServiceImpl implements AuthenticationService.Iface
{
    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    
    private final ThriftOperation<CreateApplicationTokenRequest, CreateApplicationTokenResponse> createApplicationTokenOperation;
    private final ThriftOperation<CreateUserTokenRequest, CreateUserTokenResponse> createUserTokenOperation;
    private final ThriftOperation<GetApplicationTokenInfoRequest, GetApplicationTokenInfoResponse> getApplicationTokenInfoOperation;
    private final ThriftOperation<GetUserTokenInfoRequest, GetUserTokenInfoResponse> getUserTokenInfoOperation;
    private final ThriftOperation<InvalidateApplicationTokenRequest, InvalidateApplicationTokenResponse> invalidateApplicationTokenOperation;
    private final ThriftOperation<InvalidateUserTokenRequest, InvalidateUserTokenResponse> invalidateUserTokenOperation;
    private final ThriftOperation<VerifyApplicationTokenRequest, VerifyApplicationTokenResponse> verifyApplicationTokenOperation;
    private final ThriftOperation<VerifyUserTokenRequest, VerifyUserTokenResponse> verifyUserTokenOperation;

    @Inject
    AuthenticationServiceImpl(ThriftOperation<CreateApplicationTokenRequest, CreateApplicationTokenResponse> createApplicationTokenOperation,
                              ThriftOperation<CreateUserTokenRequest, CreateUserTokenResponse> createUserTokenOperation,
                              ThriftOperation<GetApplicationTokenInfoRequest, GetApplicationTokenInfoResponse> getApplicationTokenInfoOperation,
                              ThriftOperation<GetUserTokenInfoRequest, GetUserTokenInfoResponse> getUserTokenInfoOperation,
                              ThriftOperation<InvalidateApplicationTokenRequest, InvalidateApplicationTokenResponse> invalidateApplicationTokenOperation,
                              ThriftOperation<InvalidateUserTokenRequest, InvalidateUserTokenResponse> invalidateUserTokenOperation,
                              ThriftOperation<VerifyApplicationTokenRequest, VerifyApplicationTokenResponse> verifyApplicationTokenOperation,
                              ThriftOperation<VerifyUserTokenRequest, VerifyUserTokenResponse> verifyUserTokenOperation)
    {
        checkThat(createApplicationTokenOperation,
                  createUserTokenOperation,
                  getApplicationTokenInfoOperation,
                  getUserTokenInfoOperation,
                  invalidateApplicationTokenOperation,
                  invalidateUserTokenOperation,
                  verifyApplicationTokenOperation,
                  verifyUserTokenOperation)
            .are(notNull());
        
        this.createApplicationTokenOperation = createApplicationTokenOperation;
        this.createUserTokenOperation = createUserTokenOperation;
        this.getApplicationTokenInfoOperation = getApplicationTokenInfoOperation;
        this.getUserTokenInfoOperation = getUserTokenInfoOperation;
        this.invalidateApplicationTokenOperation = invalidateApplicationTokenOperation;
        this.invalidateUserTokenOperation = invalidateUserTokenOperation;
        this.verifyApplicationTokenOperation = verifyApplicationTokenOperation;
        this.verifyUserTokenOperation = verifyUserTokenOperation;
    }
    
    
    
    @Override
    public double getApiVersion() throws TException
    {
        return AuthenticationServiceConstants.API_VERSION;
    }
    
    @Override
    public CreateApplicationTokenResponse createApplicationToken(CreateApplicationTokenRequest request) throws OperationFailedException,
                                                                                                               TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return createApplicationTokenOperation.process(request);
    }
    
    @Override
    public CreateUserTokenResponse createUserToken(CreateUserTokenRequest request) throws OperationFailedException,
                                                                                          TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return createUserTokenOperation.process(request);
    }
    
    @Override
    public GetApplicationTokenInfoResponse getApplicationTokenInfo(GetApplicationTokenInfoRequest request) throws OperationFailedException,
                                                                                                                  InvalidTokenException,
                                                                                                                  TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return getApplicationTokenInfoOperation.process(request);
    }
    
    @Override
    public GetUserTokenInfoResponse getUserTokenInfo(GetUserTokenInfoRequest request) throws OperationFailedException,
                                                                                             InvalidTokenException, TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return getUserTokenInfoOperation.process(request);
    }
    
    @Override
    public InvalidateApplicationTokenResponse invalidateApplicationToken(InvalidateApplicationTokenRequest request) throws OperationFailedException,
                                                                                                                           InvalidTokenException,
                                                                                                                           TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return invalidateApplicationTokenOperation.process(request);
    }
    
    @Override
    public InvalidateUserTokenResponse invalidateUserToken(InvalidateUserTokenRequest request) throws OperationFailedException,
                                                                                                      InvalidTokenException,
                                                                                                      TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return invalidateUserTokenOperation.process(request);
    }
    
    @Override
    public VerifyApplicationTokenResponse verifyApplicationToken(VerifyApplicationTokenRequest request) throws OperationFailedException,
                                                                                                               InvalidTokenException,
                                                                                                               TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return verifyApplicationTokenOperation.process(request);
    }
    
    @Override
    public VerifyUserTokenResponse verifyUserToken(VerifyUserTokenRequest request) throws OperationFailedException,
                                                                                          InvalidTokenException,
                                                                                          TException
    {
        AuthenticationAssertions.checkRequestNotNull(request);
        
        return verifyUserTokenOperation.process(request);
    }
    
}
