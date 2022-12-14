/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test suite for {@link BaseSiteMatchingFilter}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BaseSiteMatchingFilterTest
{
	private static final String CURRENT_URL = "/customerlists";
	private static final String EXCLUDED_URL = ".*swagger.*";
	static final String UNKNOWN_BASE_SITE = "unknownBaseSiteId";
	static final String BASE_SITE_PARAM = "baseSite";
	private BaseSiteMatchingFilter baseSiteMatchingFilter;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private FilterChain filterChain;
	@Mock
	private BaseSiteModel baseSiteModel;
	@Mock
	private BaseSiteModel currentBaseSiteModel;

	@Before
	public void setUp()
	{
		baseSiteMatchingFilter = new BaseSiteMatchingFilter();
		baseSiteMatchingFilter.setBaseSiteService(baseSiteService);
		baseSiteMatchingFilter.setExcludedUrls(Arrays.asList(EXCLUDED_URL));
		Mockito.lenient().when(httpServletRequest.getPathInfo()).thenReturn(CURRENT_URL);
		given(httpServletRequest.getDispatcherType()).willReturn(DispatcherType.REQUEST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullPathInfo() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(null);

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, never()).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(baseSiteService, never()).setCurrentBaseSite(anyString(), anyBoolean());
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test(expected = ServletException.class)
	public void testUnknownBaseSite() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(UNKNOWN_BASE_SITE);

		given(baseSiteService.getBaseSiteForUID(UNKNOWN_BASE_SITE)).willReturn(null);

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testKnownBaseSite() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(BASE_SITE_PARAM);
		given(baseSiteService.getBaseSiteForUID(BASE_SITE_PARAM)).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, times(1)).setCurrentBaseSite(baseSiteModel, true);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testBaseSiteThatEqualsCurrentSite() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(BASE_SITE_PARAM);
		given(baseSiteService.getBaseSiteForUID(BASE_SITE_PARAM)).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, never()).setCurrentBaseSite(baseSiteModel, true);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testFilterNotInvokedForExcludedUrl() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn("/webjars/springfox-swagger-ui/images/throbber.gif");

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, never()).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(baseSiteService, never()).setCurrentBaseSite(anyString(), anyBoolean());
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

}
