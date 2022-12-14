/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.previewwebservices.populators;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.util.LocalViewExecutor;

import java.util.Date;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PreviewTicketReversePopulatorTest
{
	private static final String SITE_URL = "https://host:9002/mockstorefront?site=";
	private static final String ANY_PAGE_ID = "anyPageId";
	private static final String PAGE_ID = "somePageId";
	private static final Date TEST_DATE = new Date();

	@InjectMocks
	private PreviewTicketReversePopulator populator;
	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private SessionService sessionService;
	@Mock
	private LocalViewExecutor localViewExecutor;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSSiteModel dummySite;
	@Mock
	private PreviewTicketWsDTO source;
	@Mock
	private AbstractPageModel page;

	@Before
	public void setUp() throws CMSItemNotFoundException
	{
		when(cmsSiteService.getSiteForURL(any())).thenReturn(dummySite);
		when(source.getResourcePath()).thenReturn(SITE_URL);
		when(source.getPageId()).thenReturn(ANY_PAGE_ID);
		when(cmsPageService.getPageForId(ANY_PAGE_ID)).thenReturn(page);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(populator.getLocalViewExecutor()).executeWithAllCatalogs(any());
	}

	@Test
	public void shouldSetPreviewTimeValueInSession()
	{
		// GIVEN
		when(source.getTime()).thenReturn(TEST_DATE);
		final PreviewDataModel target = new PreviewDataModel();

		// WHEN
		populator.populate(source, target);

		// THEN
		assertThat(target.getTime(), equalTo(TEST_DATE));
		verify(sessionService).setAttribute(Cms2Constants.PREVIEW_TIME, TEST_DATE);
	}

	@Test
	public void shouldNotSetPreviewTimeValueInSession()
	{
		// GIVEN
		final PreviewDataModel target = new PreviewDataModel();

		// WHEN
		populator.populate(source, target);

		// THEN
		assertThat(target.getTime(), nullValue());
		verify(sessionService).removeAttribute(Cms2Constants.PREVIEW_TIME);
	}

	@Test
	public void shouldCallCMSAdminSiteServiceIfSiteIdProvided()
	{
		// GIVEN
		final PreviewDataModel target = new PreviewDataModel();
		when(source.getSiteId()).thenReturn(PAGE_ID);

		// WHEN
		populator.populate(source, target);

		// THEN
		verify(cmsAdminSiteService).getSiteForId(PAGE_ID);
	}

	@Test
	public void shouldCallCmsSiteServiceIfSiteIdNotProvided() throws CMSItemNotFoundException
	{
		// GIVEN
		final PreviewDataModel target = new PreviewDataModel();

		// WHEN
		populator.populate(source, target);

		// THEN
		verify(cmsSiteService).getSiteForURL(any());
	}
}
