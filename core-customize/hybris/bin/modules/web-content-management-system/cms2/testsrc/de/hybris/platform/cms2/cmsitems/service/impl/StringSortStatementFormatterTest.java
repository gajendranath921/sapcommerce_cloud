/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.cmsitems.service.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class StringSortStatementFormatterTest
{
	@InjectMocks
	private StringSortStatementFormatter stringFormatter;

	@Mock
	AttributeDescriptorModel attributeDescriptor;
	@Mock
	private TypeModel attributeType;

	@Test
	public void shouldBeApplicableWithFullClassName()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn("java.lang.String");

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertThat(result, is(true));
	}

	@Test
	public void shouldNotBeApplicableWithClassSimpleName()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn("String");

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertThat(result, is(false));
	}

	@Test
	public void shouldNotBeApplicableWithInvalidClassName()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn("INVALID");

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertThat(result, is(false));
	}

}
