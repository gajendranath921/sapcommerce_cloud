/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCmsItemModelUniqueIdentifierConverterTest
{

	private static final java.lang.String UID = "uid";
	private static final java.lang.String CATALOG_ID = "catalog-id";
	private static final java.lang.String CATALOG_VERSION = "catalog-version-id";

	@Mock
	private CMSAdminItemService cmsAdminItemService;

	@Mock
	private Converter<CMSItemModel, ItemData> cmsItemModelItemDataConverter;


	@Mock
	private ObjectFactory<ItemData> itemDataDataFactory;

	@Mock
	private ItemData itemData;

	@Mock
	private CMSItemModel itemModel;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CatalogModel catalog;

	@InjectMocks
	private DefaultCmsItemModelUniqueIdentifierConverter conversionFunction;

	@Before
	public void setup() throws CMSItemNotFoundException 
	{
		when(itemModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalog.getId()).thenReturn(CATALOG_ID);
		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		when(cmsItemModelItemDataConverter.convert(itemModel)).thenReturn(itemData);
		when(cmsAdminItemService.findByUid(UID)).thenReturn(itemModel);
		when(itemDataDataFactory.getObject()).thenReturn(itemData);
	}
	
	@Test
	public void testConversionWithCMSItemModelClass()
	{
		final ItemData result = conversionFunction.convert(itemModel);
		assertThat(result, is(itemData));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void whenItemDataIsNullconvertWithCMSItemDataWillThrowException()
	{
		conversionFunction.convert((ItemData)null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void whenItemDataIdIsEmptyconvertWithCMSItemDataWillThrowException()
	{
		conversionFunction.convert(itemData);
	}

	@Test
	public void convertWithCMSItemData()
	{
		when(itemData.getItemId()).thenReturn(UID);
		final ItemModel result = conversionFunction.convert(itemData);
		assertThat(result, is(itemModel));
	}

}
