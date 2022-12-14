/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentPageService;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PrimaryPageWithLabelExistsPredicateTest
{
	private static final String LABEL = "test-label";
	private static final String UID = "uid";
	private static final String ORIGINAL_UID = "original-uid";

	@Mock
	private OriginalClonedItemProvider originalClonedItemProvider;
	@Mock
	private PageVariationResolver<ContentPageModel> resolver;
	@Mock
	private CMSContentPageService contentPageService;
	@InjectMocks
	private PrimaryPageWithLabelExistsPredicate predicate;

	@Mock
	private ContentPageModel contentPage;
	@Mock
	private ContentPageModel originalContentPage;


	@Before
	public void setup()
	{
		when(originalClonedItemProvider.getCurrentItem()).thenReturn(originalContentPage);
		when(originalContentPage.getUid()).thenReturn(ORIGINAL_UID);
		when(originalContentPage.getDefaultPage()).thenReturn(true);
		when(contentPage.getUid()).thenReturn(UID);
		when(contentPage.getDefaultPage()).thenReturn(true);
		when(contentPage.getLabel()).thenReturn(LABEL);
		when(originalContentPage.getLabel()).thenReturn(LABEL);
		when(contentPageService.findLabelVariations(LABEL, true)).thenReturn(Arrays.asList(LABEL, "/" + LABEL));
	}

	@Test
	public void shouldFindPageWithLabel()
	{
		when(resolver.findPagesByType(ContentPageModel._TYPECODE, true))
				.thenReturn(Arrays.asList(contentPage, originalContentPage));

		final boolean result = predicate.test(LABEL);
		assertTrue("Should find at least one page with same label.", result);
	}

	@Test
	public void shouldFindPageWithSlashLabel()
	{
		when(contentPage.getLabel()).thenReturn("/" + LABEL);
		when(resolver.findPagesByType(ContentPageModel._TYPECODE, true))
				.thenReturn(Arrays.asList(contentPage, originalContentPage));

		final boolean result = predicate.test(LABEL);
		assertTrue("Should find at least one page with same label.", result);
	}

	@Test
	public void shouldNotFindPageWithLabelWhenMatchSelf()
	{
		when(resolver.findPagesByType(ContentPageModel._TYPECODE, true)).thenReturn(Arrays.asList(originalContentPage));

		final boolean result = predicate.test(LABEL);
		assertFalse("Should not find pages when it matches with the same page in session.", result);
	}


	@Test
	public void shouldNotFindPageWithLabel_Empty()
	{
		when(originalClonedItemProvider.getCurrentItem()).thenReturn(contentPage);
		when(contentPage.getUid()).thenReturn(UID);
		when(resolver.findPagesByType(ContentPageModel._TYPECODE, true)).thenReturn(Collections.emptyList());

		final boolean result = predicate.test(LABEL);
		assertFalse("Should not find pages when no pages are returned.", result);
	}

}
