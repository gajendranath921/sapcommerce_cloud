/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smartedit.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.hybris.smartedit.security.SecurityError.AUTHENTICATION_ERROR_BAD_CREDENTIALS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LoginAuthenticationFailureHandlerTest {

    private static final FlashMap EXPECTED_FLASH_MAP = new FlashMap();
    {
        EXPECTED_FLASH_MAP.put("errorcode", AUTHENTICATION_ERROR_BAD_CREDENTIALS.getCode());
    }

    @Mock
    private FlashMapManager flashMapManager;

    @InjectMocks
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    @Captor
    private ArgumentCaptor<FlashMap> flashMapCaptor;

    @Test
    public void flash_map_is_created_then_errorcode_added_to_attributes_then_flash_map_is_saved() throws Exception
    {
        when(request.getSession()).thenReturn(new MockHttpSession());
        when(exception.getMessage()).thenReturn("Authentication Exception");
        lenient().doNothing().when(response).sendError(401, "Authentication Failed");

        loginAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);

        verify(flashMapManager).saveOutputFlashMap(flashMapCaptor.capture(), any(), any());

        assertThat(flashMapCaptor.getValue(), is(EXPECTED_FLASH_MAP));
    }
}
