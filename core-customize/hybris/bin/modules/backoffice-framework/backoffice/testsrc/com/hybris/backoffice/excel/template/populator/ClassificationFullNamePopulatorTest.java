/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.template.AttributeNameFormatter;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationFullNamePopulatorTest
{

	@Mock
	AttributeNameFormatter<ExcelClassificationAttribute> mockedAttributeNameFormatter;
	@InjectMocks
	ClassificationFullNamePopulator populator;

	@Test
	public void shouldGetClassificationFullName()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		given(mockedAttributeNameFormatter.format(any())).willReturn("fullName");

		// when
		final String result = populator.apply(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// them
		assertThat(result).isEqualTo("fullName");
	}
}
