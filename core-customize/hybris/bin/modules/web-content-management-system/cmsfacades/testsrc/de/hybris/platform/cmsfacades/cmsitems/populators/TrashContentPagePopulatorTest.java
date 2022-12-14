/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class TrashContentPagePopulatorTest
{
	private Map<String, Object> source;

	@Mock
	private ContentPageModel contentPageModel;

	@InjectMocks
	private TrashContentPagePopulator trashContentPagePopulator;


	@Before
	public void setUp()
	{
		source = new HashMap<>();
	}

	@Test
	public void givenProvidedPageIsNotHomePageAndIsBeingTrashed_WhenPopulated_ThenNothingHappens()
	{
		// GIVEN
		isHomePage(false);
		isPageTrashed(true);

		// WHEN
		trashContentPagePopulator.populate(source, contentPageModel);

		// THEN
		verify(contentPageModel, never()).setHomepage(true);
	}

	@Test
	public void givenProvidedPageIsNotHomePageAndIsNotBeingTrashed_WhenPopulated_ThenNothingHappens()
	{
		// GIVEN
		isHomePage(false);
		isPageTrashed(false);

		// WHEN
		trashContentPagePopulator.populate(source, contentPageModel);

		// THEN
		verify(contentPageModel, never()).setHomepage(true);
	}

	@Test
	public void givenProvidedPageIsHomePageAndIsNotBeingTrashed_WhenPopulated_ThenNothingHappens()
	{
		// GIVEN
		isHomePage(true);
		isPageTrashed(false);

		// WHEN
		trashContentPagePopulator.populate(source, contentPageModel);

		// THEN
		verify(contentPageModel, never()).setHomepage(false);
	}

	@Test
	public void givenProvidedPageIsHomePageAndItIsBeingTrashed_WhenPopulated_ThenItsHomepageFlagIsNegated()
	{
		// GIVEN
		isHomePage(true);
		isPageTrashed(true);

		// WHEN
		trashContentPagePopulator.populate(source, contentPageModel);

		// THEN
		verify(contentPageModel, times(1)).setHomepage(false);
	}

	protected void isHomePage(final boolean isHomePage)
	{
		when(contentPageModel.isHomepage()).thenReturn(isHomePage);
	}

	protected void isPageTrashed(final boolean isPageTrashed)
	{
		when(contentPageModel.getPageStatus()).thenReturn((isPageTrashed) ? CmsPageStatus.DELETED : CmsPageStatus.ACTIVE);
	}
}
