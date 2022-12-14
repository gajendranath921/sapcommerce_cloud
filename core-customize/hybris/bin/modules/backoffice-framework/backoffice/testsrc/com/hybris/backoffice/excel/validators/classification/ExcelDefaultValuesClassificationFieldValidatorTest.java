/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.validators.classification;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;


@RunWith(MockitoJUnitRunner.class)
public class ExcelDefaultValuesClassificationFieldValidatorTest
{

	private final ExcelDefaultValuesClassificationFieldValidator validator = new ExcelDefaultValuesClassificationFieldValidator();

	@Test
	public void shouldValidatorBeInvokedWhenImportParametersContainsFormatErrors()
	{
		// given
		final ImportParameters importParameters = mock(ImportParameters.class);
		given(importParameters.hasFormatErrors()).willReturn(true);

		// when
		final boolean output = validator.canHandle(mock(ExcelClassificationAttribute.class), importParameters);

		// then
		assertTrue(output);
	}

}
