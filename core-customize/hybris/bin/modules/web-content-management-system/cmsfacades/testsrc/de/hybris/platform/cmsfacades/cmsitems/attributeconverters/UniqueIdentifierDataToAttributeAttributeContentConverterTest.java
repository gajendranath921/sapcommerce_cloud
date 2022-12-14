/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class UniqueIdentifierDataToAttributeAttributeContentConverterTest
{

	public static class ModelClass extends ItemModel
	{
		// Intentionally left empty.
	}

	private static final String INVALID_ITEM_UID = "INVALID_ITEM_UID";
	private static final String ITEM_UID = "ITEM_UID";

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Mock
	private ModelClass cmsItem;

	@InjectMocks
	private final UniqueIdentifierDataToAttributeContentConverter<ModelClass> converter = new UniqueIdentifierDataToAttributeContentConverter<ModelClass>()
	{
		// Intentionally left empty.
	};



	@Before
	public void setup()
	{

		converter.setModelClass(ModelClass.class);
		when(uniqueItemIdentifierService.getItemModel(ITEM_UID, ModelClass.class)).thenReturn(ofNullable(cmsItem));
		when(uniqueItemIdentifierService.getItemModel(INVALID_ITEM_UID, ModelClass.class)).thenReturn(empty());

	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void shouldConvertValidUUID()
	{
		final ModelClass convertedItem = converter.convert(ITEM_UID);

		assertThat(convertedItem, is(cmsItem));
	}

	@Test(expected = ConversionException.class)
	public void shouldThrowExceptionForInvalidUUID()
	{
		converter.convert(INVALID_ITEM_UID);
	}


}
