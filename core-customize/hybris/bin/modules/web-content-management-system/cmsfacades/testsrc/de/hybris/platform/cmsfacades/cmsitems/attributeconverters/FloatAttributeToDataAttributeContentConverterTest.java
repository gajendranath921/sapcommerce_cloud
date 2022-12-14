/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class FloatAttributeToDataAttributeContentConverterTest
{
	
	private DefaultAttributeToDataContentConverter converter = new DefaultAttributeToDataContentConverter();
	
	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}


	@Test
	public void whenConvertNoDecimalValueReturnsAsIss()
	{
		assertThat(converter.convert(12f), is(12.0f));
	}

	@Test
	public void whenConvertDecimalValueReturnsAsIs()
	{
		assertThat(converter.convert(123.123f), is(123.123f));
	}

	@Test
	public void whenConvertMaxValueReturnsAsIs()
	{
		assertThat(converter.convert(Float.MAX_VALUE), is(Float.MAX_VALUE));
	}

	@Test
	public void whenConvertMinValueReturnsAsIs()
	{
		assertThat(converter.convert(Float.MIN_VALUE), is(Float.MIN_VALUE));
	}

}
