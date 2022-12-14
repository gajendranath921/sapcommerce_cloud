/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.populator.appender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;


@RunWith(MockitoJUnitRunner.class)
public class MultivalueExcelMarkAppenderTest
{

	private MultivalueExcelMarkAppender appender = new MultivalueExcelMarkAppender();

	@Test
	public void shouldMultivalueMarkBeAppendedWhenAttributeIsMultivalue()
	{
		// given
		final ExcelAttributeDescriptorAttribute excelAttributeDescriptorAttribute = mock(ExcelAttributeDescriptorAttribute.class);
		given(excelAttributeDescriptorAttribute.isMultiValue()).willReturn(true);

		final String input = "Article Number";

		// when
		final String output = appender.apply(input, excelAttributeDescriptorAttribute);

		// then
		assertThat(output).isEqualTo(input + ExcelTemplateConstants.SpecialMark.MULTIVALUE.getMark());
	}

	@Test
	public void shouldMultivalueMarkNotBeAppendedWhenAttributeIsNotMultivalue()
	{
		// given
		final ExcelAttributeDescriptorAttribute excelAttributeDescriptorAttribute = mock(ExcelAttributeDescriptorAttribute.class);
		given(excelAttributeDescriptorAttribute.isMultiValue()).willReturn(false);

		final String input = "Article Number";

		// when
		final String output = appender.apply(input, excelAttributeDescriptorAttribute);

		// then
		assertThat(output).isEqualTo(input).doesNotContain(String.valueOf(ExcelTemplateConstants.SpecialMark.MULTIVALUE.getMark()));
	}

}
