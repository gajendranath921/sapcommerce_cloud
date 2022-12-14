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
public class ExcelBooleanValueConverterTest
{

	private final ExcelValueConverter converter = new ExcelBooleanValueConverter();

	@Test
	public void shouldBeEligibleToConvertingWhenTypeIsBoolean()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.getType()).willReturn(Boolean.class.getName());

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters("true"));

		// then
		assertThat(canConvert).isTrue();
	}

	@Test
	public void shouldNotBeEligibleToConvertingWhenTypeIsNotBoolean()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		Mockito.lenient().when(excelAttribute.getType()).thenReturn(StringUtils.EMPTY);

		// when
		final boolean canConvert = converter.canConvert(excelAttribute, prepareImportParameters(StringUtils.EMPTY));

		// then
		assertThat(canConvert).isFalse();
	}

	@Test
	public void shouldConvertCorrectTrueValueToTrue()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final ImportParameters importParameters = prepareImportParameters("true");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Boolean.class);
		assertThat((Boolean) convertedValue).isTrue();
	}

	@Test
	public void shouldConvertCorrectFalseValueToFalse()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final ImportParameters importParameters = prepareImportParameters("false");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Boolean.class);
		assertThat((Boolean) convertedValue).isFalse();
	}

	@Test
	public void shouldConvertIncorrectValueToFalse()
	{
		// given
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		final ImportParameters importParameters = prepareImportParameters("abc");

		// when
		final Object convertedValue = converter.convert(excelAttribute, importParameters);

		// then
		assertThat(convertedValue).isInstanceOf(Boolean.class);
		assertThat((Boolean) convertedValue).isFalse();
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		return new ImportParameters(null, null, cellValue, null, Collections.emptyList());
	}
}
