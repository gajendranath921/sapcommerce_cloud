/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.validation.model.constraints.jsr303.DecimalMaxConstraintModel;
import de.hybris.platform.validation.model.constraints.jsr303.DecimalMinConstraintModel;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.Sets;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DecimalAttributeContentValidatorTest
{

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@InjectMocks
	private DecimalAttributeContentValidator validator;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ValidationErrors validationErrors;
	@Mock
	private DecimalMinConstraintModel minConstraint;
	@Mock
	private DecimalMaxConstraintModel maxConstraint;
	
	private BigDecimal minValue = new BigDecimal(10);
	private BigDecimal maxValue = new BigDecimal(20);

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		
		when(attributeDescriptor.getConstraints()).thenReturn(Sets.newHashSet(minConstraint, maxConstraint));
		
		when(minConstraint.getItemtype()).thenReturn(DecimalMinConstraintModel._TYPECODE);
		when(minConstraint.getValue()).thenReturn(minValue);

		when(maxConstraint.getItemtype()).thenReturn(DecimalMaxConstraintModel._TYPECODE);
		when(maxConstraint.getValue()).thenReturn(maxValue);
	}

	@Test
	public void testValidValue_shouldNotAddError()
	{
		
		validator.validate(new BigDecimal(15), attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}

	@Test
	public void testInValidValueGreater_shouldAddError()
	{
		final List<ValidationError> errors = validator.validate(new BigDecimal(25), attributeDescriptor);
		assertThat(errors, not(empty()));
	}
	
	@Test
	public void testInValidValueLess_shouldAddError()
	{
		final List<ValidationError> errors = validator.validate(new BigDecimal(5), attributeDescriptor);
		assertThat(errors, not(empty()));
	}
}
