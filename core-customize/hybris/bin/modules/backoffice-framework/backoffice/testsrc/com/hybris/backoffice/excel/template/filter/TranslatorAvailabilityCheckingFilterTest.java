/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.filter;


import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.translators.ExcelTranslatorRegistry;
import com.hybris.backoffice.excel.translators.ExcelValueTranslator;


@RunWith(MockitoJUnitRunner.class)
public class TranslatorAvailabilityCheckingFilterTest
{
	@Mock
	ExcelTranslatorRegistry mockedExcelTranslatorRegistry;
	@InjectMocks
	TranslatorAvailabilityCheckingFilter filter;

	@Test
	public void shouldFilterOutAttributesNotPresentInTranslatorRegistry()
	{
		// given
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(mockedExcelTranslatorRegistry.getTranslator(attributeDescriptorModel)).willReturn(Optional.empty());

		// when
		final boolean result = filter.test(attributeDescriptorModel);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotFilterOutAttributesPresentInTranslatorRegistry()
	{
		// given
		final ExcelValueTranslator excelValueTranslator = mock(ExcelValueTranslator.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(mockedExcelTranslatorRegistry.getTranslator(attributeDescriptorModel)).willReturn(Optional.of(excelValueTranslator));

		// when
		final boolean result = filter.test(attributeDescriptorModel);

		// then
		assertThat(result).isTrue();
	}
}
