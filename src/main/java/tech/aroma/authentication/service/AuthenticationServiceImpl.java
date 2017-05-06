
package tech.aroma.authentication.service;

/*
 * Copyright 2017 RedRoma, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
import javax.inject.Inject;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aroma.thrift.AromaConstants;
import tech.aroma.thrift.authentication.service.AuthenticationService;
import tech.aroma.thrift.authentication.service.CreateTokenRequest;
import tech.aroma.thrift.authentication.service.CreateTokenResponse;
import tech.aroma.thrift.authentication.service.GetTokenInfoRequest;
import tech.aroma.thrift.authentication.service.GetTokenInfoResponse;
import tech.aroma.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.thrift.authentication.service.VerifyTokenRequest;
import tech.aroma.thrift.authentication.service.VerifyTokenResponse;
import tech.aroma.thrift.exceptions.InvalidArgumentException;
import tech.aroma.thrift.exceptions.InvalidTokenException;
import tech.aroma.thrift.exceptions.OperationFailedException;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static tech.aroma.thrift.assertions.AromaAssertions.checkRequestNotNull;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 * This is the Implementation of the {@linkplain AuthenticationService.Iface Aroma Authentication Service.}
 *
 * @author SirWellington
 */
@Internal
final class AuthenticationServiceImpl implements AuthenticationService.Iface
{

    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final ThriftOperation<CreateTokenRequest, CreateTokenResponse> createTokenOperation;
    private final ThriftOperation<GetTokenInfoRequest, GetTokenInfoResponse> getTokenInfoOperation;
    private final ThriftOperation<InvalidateTokenRequest, InvalidateTokenResponse> invalidateTokenOperation;
    private final ThriftOperation<VerifyTokenRequest, VerifyTokenResponse> verifyTokenOperation;

    @Inject
    AuthenticationServiceImpl(ThriftOperation<CreateTokenRequest, CreateTokenResponse> createTokenOperation,
                              ThriftOperation<GetTokenInfoRequest, GetTokenInfoResponse> getTokenInfoOperation,
                              ThriftOperation<InvalidateTokenRequest, InvalidateTokenResponse> invalidateTokenOperation,
                              ThriftOperation<VerifyTokenRequest, VerifyTokenResponse> verifyTokenOperation)
    {
        checkThat(createTokenOperation,
                  getTokenInfoOperation,
                  invalidateTokenOperation,
                  verifyTokenOperation)
            .are(notNull());

        this.createTokenOperation = createTokenOperation;
        this.getTokenInfoOperation = getTokenInfoOperation;
        this.invalidateTokenOperation = invalidateTokenOperation;
        this.verifyTokenOperation = verifyTokenOperation;
    }

    @Override
    public double getApiVersion() throws TException
    {
        return AromaConstants.API_VERSION;
    }

    @Override
    public CreateTokenResponse createToken(CreateTokenRequest request) throws OperationFailedException,
                                                                              InvalidArgumentException,
                                                                              TException
    {
        checkRequestNotNull(request);

        return createTokenOperation.process(request);
    }

    @Override
    public GetTokenInfoResponse getTokenInfo(GetTokenInfoRequest request) throws OperationFailedException,
                                                                                 InvalidArgumentException,
                                                                                 InvalidTokenException,
                                                                                 TException
    {
        checkRequestNotNull(request);

        return getTokenInfoOperation.process(request);
    }

    @Override
    public InvalidateTokenResponse invalidateToken(InvalidateTokenRequest request) throws OperationFailedException,
                                                                                          InvalidArgumentException,
                                                                                          InvalidTokenException,
                                                                                          TException
    {
        checkRequestNotNull(request);

        return invalidateTokenOperation.process(request);
    }

    @Override
    public VerifyTokenResponse verifyToken(VerifyTokenRequest request) throws OperationFailedException,
                                                                              InvalidArgumentException,
                                                                              InvalidTokenException,
                                                                              TException
    {
        checkRequestNotNull(request);

        return verifyTokenOperation.process(request);
    }

}
