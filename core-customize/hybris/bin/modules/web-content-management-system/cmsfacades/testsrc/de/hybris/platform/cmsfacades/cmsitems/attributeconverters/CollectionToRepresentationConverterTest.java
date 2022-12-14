/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.CollectionToRepresentationConverter;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CMSITEMS_INVALID_CONVERSION_ERROR;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CollectionToRepresentationConverterTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String ORIGINAL_ITEM_1 = "orig_item_1";
	private final String ORIGINAL_ITEM_2 = "orig_item_2";
	private final String ORIGINAL_ITEM_3 = "orig_item_3";

	private final String TRANSFORMED_ITEM_1 = "transf_item_1";
	private final String TRANSFORMED_ITEM_2 = "transf_item_2";
	private final String TRANSFORMED_ITEM_3 = "transf_item_3";

	private final String ATTRIBUTE_QUALIFIER = "some_qualifier";

	private Collection originalCollection;

	@Mock
	private Function transformationFunction;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;

	@Mock
	private ValidationErrors validationErrors;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@InjectMocks
	private CollectionToRepresentationConverter collectionToRepresentationConverter;

	private enum ExceptionType {
		VALIDATION,
		CONVERSION,
		ANY
	}

	// --------------------------------------------------------------------------
	// Test SetUp
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		originalCollection = Arrays.asList(ORIGINAL_ITEM_1, ORIGINAL_ITEM_2, ORIGINAL_ITEM_3);

		// Transformation Function
		when(transformationFunction.apply(ORIGINAL_ITEM_1)).thenReturn(TRANSFORMED_ITEM_1);
		when(transformationFunction.apply(ORIGINAL_ITEM_2)).thenReturn(TRANSFORMED_ITEM_2);
		when(transformationFunction.apply(ORIGINAL_ITEM_3)).thenReturn(TRANSFORMED_ITEM_3);

		// Validation Errors
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);

		// Attribute Descriptor Mode
		when(attributeDescriptorModel.getQualifier()).thenReturn(ATTRIBUTE_QUALIFIER);
	}


	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenNullCollection_WhenConvertIsCalled_ThenItReturnsNull()
	{
		// WHEN
		Collection result = collectionToRepresentationConverter.convert(attributeDescriptorModel,null, transformationFunction);

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenCollection_WhenConvertIsCalled_ThenEachItemIsConvertedThroughTheTransformationFunction()
	{
		// WHEN
		Collection<Object> result = collectionToRepresentationConverter
				.convert(attributeDescriptorModel, originalCollection, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_1);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_2);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_3);
		assertThat(result, contains(TRANSFORMED_ITEM_1, TRANSFORMED_ITEM_2, TRANSFORMED_ITEM_3));
	}

	@Test
	public void givenCollection_WhenConvertIsCalled_AndOneItemReturnsNull_ThenOnlyNonNullItemsIsAdded()
	{
		// GIVEN
		when(transformationFunction.apply(ORIGINAL_ITEM_2)).thenReturn(null);

		// WHEN
		Collection<Object> result = collectionToRepresentationConverter
				.convert(attributeDescriptorModel, originalCollection, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_1);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_2);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_3);
		assertThat(result, contains(TRANSFORMED_ITEM_1, TRANSFORMED_ITEM_3));
	}

	@Test
	public void givenInvalidParameter_WhenConvertIsCalled_ThenValidationExceptionIsTriggeredAndHandled()
	{
		// GIVEN
		throwExceptionWhenFunctionExecutes(ORIGINAL_ITEM_2, ExceptionType.VALIDATION);

		// WHEN
		Collection<Object> result = collectionToRepresentationConverter
				.convert(attributeDescriptorModel, originalCollection, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_1);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_2);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_3);

		verify(validationErrorsProvider, times(1))
				.collectValidationErrors(any(), eq(Optional.empty()), eq(Optional.of(1)));

		assertThat(result, contains(TRANSFORMED_ITEM_1, TRANSFORMED_ITEM_3));
	}

	@Test
	public void givenNotSupportedParameter_WhenConvertIsCalled_ThenConversionExceptionIsTriggeredAndHandled()
	{
		// GIVEN
		ValidationError conversionError;
		ArgumentCaptor<ValidationError> validationErrorCaptor = ArgumentCaptor.forClass(ValidationError.class);

		throwExceptionWhenFunctionExecutes(ORIGINAL_ITEM_2, ExceptionType.CONVERSION);

		// WHEN
		Collection<Object> result = collectionToRepresentationConverter
				.convert(attributeDescriptorModel, originalCollection, transformationFunction);

		// THEN
		verify(validationErrors, times(1)).add(validationErrorCaptor.capture());
		conversionError = validationErrorCaptor.getValue();

		assertThat(conversionError.getField(), is(ATTRIBUTE_QUALIFIER));
		assertThat(conversionError.getRejectedValue(), is(ORIGINAL_ITEM_2));
		assertThat(conversionError.getPosition(), is(1));
		assertThat(conversionError.getErrorCode(), is(CMSITEMS_INVALID_CONVERSION_ERROR));

		assertThat(result, contains(TRANSFORMED_ITEM_1, TRANSFORMED_ITEM_3));
	}

	@Test(expected = Exception.class)
	public void WhenConvertIsCalled_AndThereIsAnExceptionThrown_ThenExceptionIsNotCaught()
	{
		// GIVEN
		throwExceptionWhenFunctionExecutes(ORIGINAL_ITEM_2, ExceptionType.ANY);

		// WHEN
		collectionToRepresentationConverter.convert(attributeDescriptorModel, originalCollection, transformationFunction);

		// THEN
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_1);
		verify(transformationFunction, times(1)).apply(ORIGINAL_ITEM_2);
		verify(transformationFunction, never()).apply(ORIGINAL_ITEM_3);
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	public void throwExceptionWhenFunctionExecutes(String parameter, ExceptionType exceptionType)
	{
		Class exceptionClass;
		switch (exceptionType)
		{
			case VALIDATION:
				exceptionClass = ValidationException.class;
				break;
			case CONVERSION:
				exceptionClass = ConversionException.class;
				break;
			default:
				exceptionClass = Exception.class;
				break;
		}

		when(transformationFunction.apply(parameter)).thenThrow(exceptionClass);
	}
}
