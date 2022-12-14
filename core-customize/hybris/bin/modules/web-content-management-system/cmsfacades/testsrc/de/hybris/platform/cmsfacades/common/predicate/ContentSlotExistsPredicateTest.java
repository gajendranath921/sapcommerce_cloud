/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ContentSlotExistsPredicateTest
{
	private static final String VALID_CONTENT_SLOT_UID = "valid-content-slot-uid";
	private static final String INVALID_CONTENT_SLOT_UID = "invalid-content-slot-uid";

	@InjectMocks
	private final Predicate<String> predicate = new ContentSlotExistsPredicate();

	@Mock
	private CMSAdminContentSlotService contentSlotAdminService;
	@Mock
	private ContentSlotModel contentSlot;

	private String target;

	@Before
	public void setUp()
	{
		target = VALID_CONTENT_SLOT_UID;
		when(contentSlotAdminService.getContentSlotForId(target)).thenReturn(contentSlot);
	}

	@Test
	public void shouldFail_ContentSlotNotFound()
	{
		target = INVALID_CONTENT_SLOT_UID;
		when(contentSlotAdminService.getContentSlotForId(target)).thenThrow(new UnknownIdentifierException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldFail_AmbiguousComponent()
	{
		target = INVALID_CONTENT_SLOT_UID;
		when(contentSlotAdminService.getContentSlotForId(target)).thenThrow(new AmbiguousIdentifierException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldPass_ContentSlotExists()
	{
		final boolean result = predicate.test(target);
		assertTrue(result);
	}
}
