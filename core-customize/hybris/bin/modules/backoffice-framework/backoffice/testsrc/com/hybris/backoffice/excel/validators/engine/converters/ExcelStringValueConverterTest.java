/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.validators.engine.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;


@RunWith(MockitoJUnitRunner.class)
public class ExcelStringValueConverterTest
{

	private final ExcelValueConverter converter = new ExcelStringValueConverter();
	final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsString()
	{
		// given
		given(excelAttribute.getType()).willReturn(String.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldNotBeEligibleToConvertingWhenTypeIsNotString()
	{
		// given
		given(excelAttribute.getType()).willReturn(Number.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isFalse();
	}

	@Test
	public void shouldConvertValueForStringType()
	{
		// given
		Mockito.lenient().when(excelAttribute.getType()).thenReturn(String.class.getName());
		final ImportParameters importParameters = prepareImportParameters("123");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(String.class);
		assertThat(convertedValue).isEqualTo("123");
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		return new ImportParameters(null, null, cellValue, null, Collections.emptyList());
	}
}
