
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

/**
 * This is the Implementation of the {@linkplain AuthenticationService.Iface Banana Authentication Service.}
 * @author SirWellington
 */
@Internal
final class AuthenticationServiceImpl implements AuthenticationService.Iface
{
    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    
    private ThriftOperation<CreateApplicationTokenRequest, CreateApplicationTokenResponse> createApplicationTokenOperation;
    private ThriftOperation<CreateUserTokenRequest, CreateUserTokenResponse> createUserTokenOperation;
    private ThriftOperation<GetApplicationTokenInfoRequest, GetApplicationTokenInfoResponse> getApplicationTokenInfoOperation;
    private ThriftOperation<GetUserTokenInfoRequest, GetUserTokenInfoResponse> getUserTokenInfoOperation;
    private ThriftOperation<InvalidateApplicationTokenRequest, InvalidateApplicationTokenResponse> invalidateApplicationTokenOperation;
    private ThriftOperation<InvalidateUserTokenRequest, InvalidateUserTokenResponse> invalidateUserTokenOperation;
    private ThriftOperation<VerifyApplicationTokenRequest, VerifyApplicationTokenResponse> verifyApplicationTokenOperation;
    private ThriftOperation<VerifyUserTokenRequest, VerifyUserTokenResponse> verifyUserTokenOperation;

    @Override
    public double getApiVersion() throws TException
    {
        return AuthenticationServiceConstants.API_VERSION;
    }

    @Override
    public CreateApplicationTokenResponse createApplicationToken(CreateApplicationTokenRequest request) throws OperationFailedException,
                                                                                                               TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public CreateUserTokenResponse createUserToken(CreateUserTokenRequest request) throws OperationFailedException,
                                                                                          TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public GetApplicationTokenInfoResponse getApplicationTokenInfo(GetApplicationTokenInfoRequest request) throws OperationFailedException,
                                                                                                                  InvalidTokenException,
                                                                                                                  TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public GetUserTokenInfoResponse getUserTokenInfo(GetUserTokenInfoRequest request) throws OperationFailedException,
                                                                                             InvalidTokenException, TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public InvalidateApplicationTokenResponse invalidateApplicationToken(InvalidateApplicationTokenRequest request) throws OperationFailedException,
                                                                                                                           InvalidTokenException,
                                                                                                                           TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public InvalidateUserTokenResponse invalidateUserToken(InvalidateUserTokenRequest request) throws OperationFailedException,
                                                                                                      InvalidTokenException,
                                                                                                      TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public VerifyApplicationTokenResponse verifyApplicationToken(VerifyApplicationTokenRequest request) throws OperationFailedException,
                                                                                                               InvalidTokenException,
                                                                                                               TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

    @Override
    public VerifyUserTokenResponse verifyUserToken(VerifyUserTokenRequest request) throws OperationFailedException,
                                                                                          InvalidTokenException, 
                                                                                          TException
    {
throw new OperationFailedException("Not Implemented Yet!");
    }

}
