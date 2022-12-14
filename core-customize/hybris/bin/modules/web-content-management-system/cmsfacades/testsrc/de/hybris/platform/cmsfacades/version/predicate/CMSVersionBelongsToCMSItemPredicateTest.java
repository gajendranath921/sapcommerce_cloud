/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.version.predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSVersionBelongsToCMSItemPredicateTest
{
	private static final String ITEM_UUID = "item-uuid";
	private static final String VERSION_UID = "version-uid";
	private static final String ITEM_UID_1 = "item-uid-1";
	private static final String ITEM_UID_2 = "item-uid-2";

	@InjectMocks
	private CMSVersionBelongsToCMSItemPredicate predicate;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CMSVersionService cmsVersionService;

	@Mock
	private CMSItemModel cmsItemModel;
	@Mock
	private CMSVersionModel cmsVersionModel;

	@Test
	public void whenCMSVersionBelongsToCMSItemShouldReturnTrue()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsItemModel.getUid()).thenReturn(ITEM_UID_1);
		when(cmsVersionModel.getItemUid()).thenReturn(ITEM_UID_1);

		assertThat(predicate.test(ITEM_UUID, VERSION_UID), is(true));
	}

	@Test
	public void whenCMSVersionDoesNotBelongsToCMSItemShouldReturnFalse()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsItemModel.getUid()).thenReturn(ITEM_UID_1);
		when(cmsVersionModel.getItemUid()).thenReturn(ITEM_UID_2);

		assertThat(predicate.test(ITEM_UUID, VERSION_UID), is(false));
	}

	@Test
	public void whenCMSItemDoesNotExistShouldReturnFalse()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenThrow(new UnknownIdentifierException(""));
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));

		assertThat(predicate.test(ITEM_UUID, VERSION_UID), is(false));
	}

	@Test
	public void whenCMSVersionDoesNotExistShouldReturnFalse()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.empty());

		assertThat(predicate.test(ITEM_UUID, VERSION_UID), is(false));
	}
}
