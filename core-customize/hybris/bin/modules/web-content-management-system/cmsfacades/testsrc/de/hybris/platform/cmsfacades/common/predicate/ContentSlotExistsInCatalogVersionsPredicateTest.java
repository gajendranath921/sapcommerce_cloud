/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ContentSlotExistsInCatalogVersionsPredicateTest
{
	private static final String VALID_CONTENT_SLOT_UID = "valid-content-slot-uid";
	private static final String INVALID_CONTENT_SLOT_UID = "invalid-content-slot-uid";

	@InjectMocks
	private final Predicate<String> predicate = new ContentSlotExistsInCatalogVersionsPredicate();

	@Mock
	private CMSAdminContentSlotService contentSlotAdminService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private ContentSlotModel contentSlot;
	@Mock
	private CatalogVersionModel parentCatalogVersion;
	@Mock
	private CatalogVersionModel childCatalogVersion;

	private String target;
	private List<CatalogVersionModel> catalogVersions;

	@Before
	public void setUp()
	{
		catalogVersions = Arrays.asList(parentCatalogVersion, childCatalogVersion);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(catalogVersions);
	}

	@Test
	public void shouldFail_ContentSlotNotFound()
	{
		target = INVALID_CONTENT_SLOT_UID;
		when(contentSlotAdminService.getContentSlotForIdAndCatalogVersions(target, catalogVersions))
		.thenThrow(new UnknownIdentifierException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldFail_AmbiguousComponent()
	{
		target = INVALID_CONTENT_SLOT_UID;
		when(contentSlotAdminService.getContentSlotForIdAndCatalogVersions(target, catalogVersions))
		.thenThrow(new AmbiguousIdentifierException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldPass_ContentSlotExists()
	{
		target = VALID_CONTENT_SLOT_UID;
		when(contentSlotAdminService.getContentSlotForIdAndCatalogVersions(target, catalogVersions)).thenReturn(contentSlot);

		final boolean result = predicate.test(target);
		assertTrue(result);
	}
}
