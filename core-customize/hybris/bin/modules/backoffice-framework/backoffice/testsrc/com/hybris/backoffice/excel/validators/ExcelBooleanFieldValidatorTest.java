/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.validators;

import static com.hybris.backoffice.excel.validators.ExcelBooleanValidator.VALIDATION_INCORRECTTYPE_BOOLEAN_MESSAGE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelBooleanFieldValidatorTest
{

	private final ExcelBooleanValidator excelBooleanFieldValidator = new ExcelBooleanValidator();

	@Test
	public void shouldHandleWhenCellValueIsNotBlankAndAttributeIsBoolean()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "notBlank", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Boolean.class.getCanonicalName());

		// when
		final boolean canHandle = excelBooleanFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleWhenCellIsEmpty()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		Mockito.lenient().when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		Mockito.lenient().when(typeModel.getCode()).thenReturn(Boolean.class.getCanonicalName());

		// when
		final boolean canHandle = excelBooleanFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleWhenAttributeIsNotBoolean()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "true", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Integer.class.getCanonicalName());

		// when
		final boolean canHandle = excelBooleanFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotReturnValidationErrorWhenCellHasBooleanValue()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "true", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationResult = excelBooleanFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isFalse();
		assertThat(validationResult.getValidationErrors()).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenCellDoesntHaveBooleanValue()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "abc", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationResult = excelBooleanFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isTrue();
		assertThat(validationResult.getValidationErrors()).hasSize(1);
		assertThat(validationResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(
				VALIDATION_INCORRECTTYPE_BOOLEAN_MESSAGE_KEY);
		assertThat(validationResult.getValidationErrors().get(0).getParams()).containsExactly(importParameters.getCellValue());
	}

}
