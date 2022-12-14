/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.populator.appender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;


@RunWith(MockitoJUnitRunner.class)
public class UniqueExcelMarkAppenderTest
{

	@Mock
	ExcelFilter<AttributeDescriptorModel> uniqueFilter;

	@InjectMocks
	UniqueExcelMarkAppender appender = new UniqueExcelMarkAppender();

	@Test
	public void shouldMarkBeAppendedWhenExcelFilterReturnsTrue()
	{
		// given
		final String input = "Article Number";
		given(uniqueFilter.test(any())).willReturn(true);

		// when
		final String output = appender.apply(input, mock(ExcelAttributeDescriptorAttribute.class));

		// then
		assertThat(output).contains(String.valueOf(ExcelTemplateConstants.SpecialMark.UNIQUE.getMark()));
	}

	@Test
	public void shouldMarkNotBeAppendedWhenExcelFilterReturnsFalse()
	{
		// given
		final String input = "Article Number";
		given(uniqueFilter.test(any())).willReturn(false);

		// when
		final String output = appender.apply(input, mock(ExcelAttributeDescriptorAttribute.class));

		// then
		assertThat(output).isEqualTo(input).doesNotContain(String.valueOf(ExcelTemplateConstants.SpecialMark.UNIQUE.getMark()));
	}

}
