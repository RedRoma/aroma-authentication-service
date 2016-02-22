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

package tech.aroma.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.aroma.thrift.authentication.AuthenticationToken;
import tech.aroma.thrift.authentication.service.CreateTokenRequest;
import tech.aroma.thrift.authentication.service.CreateTokenResponse;
import tech.aroma.thrift.authentication.service.GetTokenInfoRequest;
import tech.aroma.thrift.authentication.service.GetTokenInfoResponse;
import tech.aroma.thrift.authentication.service.InvalidateTokenRequest;
import tech.aroma.thrift.authentication.service.InvalidateTokenResponse;
import tech.aroma.thrift.authentication.service.VerifyTokenRequest;
import tech.aroma.thrift.authentication.service.VerifyTokenResponse;
import tech.aroma.thrift.service.AromaServiceConstants;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.DontRepeat;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;
import tech.sirwellington.alchemy.thrift.operations.ThriftOperation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@Repeat(100)
@RunWith(AlchemyTestRunner.class)
public class AuthenticationServiceImplTest
{

    @Mock
    private ThriftOperation<CreateTokenRequest, CreateTokenResponse> createTokenOperation;
    @GeneratePojo
    private CreateTokenRequest createTokenRequest;
    @GeneratePojo
    private CreateTokenResponse createTokenResponse;

    @Mock
    private ThriftOperation<GetTokenInfoRequest, GetTokenInfoResponse> getTokenInfoOperation;
    @GeneratePojo
    private GetTokenInfoRequest getTokenInfoRequest;
    @GeneratePojo
    private GetTokenInfoResponse getTokenInfoResponse;

    @Mock
    private ThriftOperation<InvalidateTokenRequest, InvalidateTokenResponse> invalidateTokenOperation;
    @GeneratePojo
    private InvalidateTokenRequest invalidateTokenRequest;
    @GeneratePojo
    private InvalidateTokenResponse invalidateTokenResponse;

    @Mock
    private ThriftOperation<VerifyTokenRequest, VerifyTokenResponse> verifyTokenOperation;
    @GeneratePojo
    private VerifyTokenRequest verifyTokenRequest;
    @GeneratePojo
    private VerifyTokenResponse verifyTokenResponse;

    private AuthenticationServiceImpl instance;

    private AuthenticationToken authenticationToken;
    
    @Before
    public void setUp()
    {
        instance = new AuthenticationServiceImpl(createTokenOperation,
                                                 getTokenInfoOperation,
                                                 invalidateTokenOperation,
                                                 verifyTokenOperation);

        verifyZeroInteractions(createTokenOperation,
                               getTokenInfoOperation,
                               invalidateTokenOperation,
                               verifyTokenOperation);
        
        authenticationToken = one(TokenGenerators.authenticationTokens());
        createTokenResponse.setToken(authenticationToken);
        getTokenInfoResponse.setToken(authenticationToken);
    }

    @DontRepeat
    @Test
    public void testConstructWithNullArgument()
    {
        assertThrows(() -> new AuthenticationServiceImpl(null,
                                                           getTokenInfoOperation,
                                                           invalidateTokenOperation,
                                                           verifyTokenOperation))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> new AuthenticationServiceImpl(createTokenOperation,
                                                           null,
                                                           invalidateTokenOperation,
                                                           verifyTokenOperation))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> new AuthenticationServiceImpl(createTokenOperation,
                                                           getTokenInfoOperation,
                                                           null,
                                                           verifyTokenOperation))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> new AuthenticationServiceImpl(createTokenOperation,
                                                           getTokenInfoOperation,
                                                           invalidateTokenOperation,
                                                           null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> new AuthenticationServiceImpl(null,
                                                           null,
                                                           null,
                                                           null))
            .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void testGetApiVersion() throws Exception
    {
        double response = instance.getApiVersion();
        assertThat(response, is(AromaServiceConstants.API_VERSION));

    }

    @Test
    public void testCreateToken() throws Exception
    {
        when(createTokenOperation.process(createTokenRequest))
            .thenReturn(createTokenResponse);

        CreateTokenResponse response = instance.createToken(createTokenRequest);
        assertThat(response, is(createTokenResponse));

        verify(createTokenOperation).process(createTokenRequest);
    }

    @Test
    public void testGetTokenInfo() throws Exception
    {
        when(getTokenInfoOperation.process(getTokenInfoRequest))
            .thenReturn(getTokenInfoResponse);

        GetTokenInfoResponse response = instance.getTokenInfo(getTokenInfoRequest);
        assertThat(response, is(getTokenInfoResponse));
        verify(getTokenInfoOperation).process(getTokenInfoRequest);
    }

    @Test
    public void testInvalidateToken() throws Exception
    {

        when(invalidateTokenOperation.process(invalidateTokenRequest))
            .thenReturn(invalidateTokenResponse);

        InvalidateTokenResponse response = instance.invalidateToken(invalidateTokenRequest);
        assertThat(response, is(invalidateTokenResponse));
        verify(invalidateTokenOperation).process(invalidateTokenRequest);
    }

    @Test
    public void testVerifyToken() throws Exception
    {
        when(verifyTokenOperation.process(verifyTokenRequest))
            .thenReturn(verifyTokenResponse);

        VerifyTokenResponse response = instance.verifyToken(verifyTokenRequest);
        assertThat(response, is(verifyTokenResponse));
        verify(verifyTokenOperation).process(verifyTokenRequest);
    }
}
