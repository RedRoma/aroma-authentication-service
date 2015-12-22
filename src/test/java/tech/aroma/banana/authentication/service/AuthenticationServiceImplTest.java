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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner.class)
public class AuthenticationServiceImplTest 
{
 
    @Mock
    private ThriftOperation<CreateApplicationTokenRequest, CreateApplicationTokenResponse> createApplicationTokenOperation;
    @GeneratePojo
    private CreateApplicationTokenRequest createApplicationTokenRequest;
    @GeneratePojo
    private CreateApplicationTokenResponse createApplicationTokenResponse;

    @Mock
    private ThriftOperation<CreateUserTokenRequest, CreateUserTokenResponse> createUserTokenOperation;
    @GeneratePojo
    private CreateUserTokenRequest createUserTokenRequest;
    @GeneratePojo
    private CreateUserTokenResponse createUserTokenResponse;

    @Mock
    private ThriftOperation<GetApplicationTokenInfoRequest, GetApplicationTokenInfoResponse> getApplicationTokenInfoOperation;
    @GeneratePojo
    private GetApplicationTokenInfoRequest getApplicationTokenInfoRequest;
    @GeneratePojo
    private GetApplicationTokenInfoResponse getApplicationTokenInfoResponse;

    @Mock
    private ThriftOperation<GetUserTokenInfoRequest, GetUserTokenInfoResponse> getUserTokenInfoOperation;
    @GeneratePojo
    private GetUserTokenInfoRequest getUserTokenInfoRequest;
    @GeneratePojo
    private GetUserTokenInfoResponse getUserTokenInfoResponse;

    @Mock
    private ThriftOperation<InvalidateApplicationTokenRequest, InvalidateApplicationTokenResponse> invalidateApplicationTokenOperation;
    @GeneratePojo
    private InvalidateApplicationTokenRequest invalidateApplicationTokenRequest;
    @GeneratePojo
    private InvalidateApplicationTokenResponse invalidateApplicationTokenResponse;

    @Mock
    private ThriftOperation<InvalidateUserTokenRequest, InvalidateUserTokenResponse> invalidateUserTokenOperation;
    @GeneratePojo
    private InvalidateUserTokenRequest invalidateUserTokenRequest;
    @GeneratePojo
    private InvalidateUserTokenResponse invalidateUserTokenResponse;

    @Mock
    private ThriftOperation<VerifyApplicationTokenRequest, VerifyApplicationTokenResponse> verifyApplicationTokenOperation;
    @GeneratePojo
    private VerifyApplicationTokenRequest verifyApplicationTokenRequest;
    @GeneratePojo
    private VerifyApplicationTokenResponse verifyApplicationTokenResponse;

    @Mock
    private ThriftOperation<VerifyUserTokenRequest, VerifyUserTokenResponse> verifyUserTokenOperation;
    @GeneratePojo
    private VerifyUserTokenRequest verifyUserTokenRequest;
    @GeneratePojo
    private VerifyUserTokenResponse verifyUserTokenResponse;
    
    private AuthenticationServiceImpl instance;

    @Before
    public void setUp()
    {
        instance = new AuthenticationServiceImpl(createApplicationTokenOperation,
                                                 createUserTokenOperation,
                                                 getApplicationTokenInfoOperation,
                                                 getUserTokenInfoOperation,
                                                 invalidateApplicationTokenOperation,
                                                 invalidateUserTokenOperation,
                                                 verifyApplicationTokenOperation,
                                                 verifyUserTokenOperation);
        
        verifyZeroInteractions(createApplicationTokenOperation,
                               createUserTokenOperation,
                               getApplicationTokenInfoOperation,
                               getUserTokenInfoOperation,
                               invalidateApplicationTokenOperation,
                               invalidateUserTokenOperation,
                               verifyApplicationTokenOperation,
                               verifyUserTokenOperation);
    }
    
    @DontRepeat
    @Test
    public void testConstructWithNullArgument()
    {
        assertThrows(() -> new AuthenticationServiceImpl(null,
                                                         createUserTokenOperation,
                                                         getApplicationTokenInfoOperation,
                                                        getUserTokenInfoOperation,
                                                         invalidateApplicationTokenOperation,
                                                         invalidateUserTokenOperation,
                                                         verifyApplicationTokenOperation,
                                                         verifyUserTokenOperation))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> new AuthenticationServiceImpl(createApplicationTokenOperation,
                                                         null,
                                                         getApplicationTokenInfoOperation,
                                                         getUserTokenInfoOperation,
                                                         invalidateApplicationTokenOperation,
                                                         invalidateUserTokenOperation,
                                                         verifyApplicationTokenOperation,
                                                         verifyUserTokenOperation))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> new AuthenticationServiceImpl(null,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         null,
                                                         null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testGetApiVersion() throws Exception
    {
        double response = instance.getApiVersion();
        assertThat(response, is(AuthenticationServiceConstants.API_VERSION));
        
    }

    @Test
    public void testCreateApplicationToken() throws Exception
    {
        when(createApplicationTokenOperation.process(createApplicationTokenRequest))
            .thenReturn(createApplicationTokenResponse);
        
        CreateApplicationTokenResponse response = instance.createApplicationToken(createApplicationTokenRequest);
        assertThat(response, is(createApplicationTokenResponse));
        
        verify(createApplicationTokenOperation).process(createApplicationTokenRequest);
    }

    @Test
    public void testCreateUserToken() throws Exception
    {
        when(createUserTokenOperation.process(createUserTokenRequest))
            .thenReturn(createUserTokenResponse);
        
        CreateUserTokenResponse response = instance.createUserToken(createUserTokenRequest);
        assertThat(response, is(createUserTokenResponse));
        verify(createUserTokenOperation).process(createUserTokenRequest);
    }

    @Test
    public void testGetApplicationTokenInfo() throws Exception
    {
        when(getApplicationTokenInfoOperation.process(getApplicationTokenInfoRequest))
            .thenReturn(getApplicationTokenInfoResponse);
        
        GetApplicationTokenInfoResponse response = instance.getApplicationTokenInfo(getApplicationTokenInfoRequest);
        assertThat(response, is(getApplicationTokenInfoResponse));
        verify(getApplicationTokenInfoOperation).process(getApplicationTokenInfoRequest);
    }

    @Test
    public void testGetUserTokenInfo() throws Exception
    {
        when(getUserTokenInfoOperation.process(getUserTokenInfoRequest))
            .thenReturn(getUserTokenInfoResponse);
        
        GetUserTokenInfoResponse response = instance.getUserTokenInfo(getUserTokenInfoRequest);
        assertThat(response, is(getUserTokenInfoResponse));
        verify(getUserTokenInfoOperation).process(getUserTokenInfoRequest);
    }

    @Test
    public void testInvalidateApplicationToken() throws Exception
    {
        
        when(invalidateApplicationTokenOperation.process(invalidateApplicationTokenRequest))
            .thenReturn(invalidateApplicationTokenResponse);
        
        InvalidateApplicationTokenResponse response = instance.invalidateApplicationToken(invalidateApplicationTokenRequest);
        assertThat(response, is(invalidateApplicationTokenResponse));
        verify(invalidateApplicationTokenOperation).process(invalidateApplicationTokenRequest);
    }

    @Test
    public void testInvalidateUserToken() throws Exception
    {
        
        when(invalidateUserTokenOperation.process(invalidateUserTokenRequest))
            .thenReturn(invalidateUserTokenResponse);
        
        InvalidateUserTokenResponse response = instance.invalidateUserToken(invalidateUserTokenRequest);
        assertThat(response, is(invalidateUserTokenResponse));
        verify(invalidateUserTokenOperation).process(invalidateUserTokenRequest);
    }

    @Test
    public void testVerifyApplicationToken() throws Exception
    {
        when(verifyApplicationTokenOperation.process(verifyApplicationTokenRequest))
            .thenReturn(verifyApplicationTokenResponse);
        
        VerifyApplicationTokenResponse response = instance.verifyApplicationToken(verifyApplicationTokenRequest);
        assertThat(response, is(verifyApplicationTokenResponse));
        verify(verifyApplicationTokenOperation).process(verifyApplicationTokenRequest);
    }

    @Test
    public void testVerifyUserToken() throws Exception
    {
        
        when(verifyUserTokenOperation.process(verifyUserTokenRequest))
            .thenReturn(verifyUserTokenResponse);
        
        VerifyUserTokenResponse response = instance.verifyUserToken(verifyUserTokenRequest);
        assertThat(response, is(verifyUserTokenResponse));
        verify(verifyUserTokenOperation).process(verifyUserTokenRequest);
    }

}