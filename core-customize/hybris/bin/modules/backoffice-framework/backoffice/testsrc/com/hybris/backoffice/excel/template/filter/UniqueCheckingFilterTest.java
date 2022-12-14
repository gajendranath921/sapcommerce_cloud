/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Collections;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UniqueCheckingFilterTest
{

	private UniqueCheckingFilter filter = new UniqueCheckingFilter();

	@Test
	public void shouldReturnFalseWhenAttributeDescriptorIsNotUniqueAndItsNotReturnedByComposedType()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		given(composedType.getUniqueKeyAttributes()).willReturn(Collections.emptyList());
		given(model.getEnclosingType()).willReturn(composedType);
		given(model.getUnique()).willReturn(false);

		// when
		final boolean result = filter.test(model);

		// then
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueWhenAttributeDescriptorIsUnique()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		given(model.getUnique()).willReturn(true);

		// when
		final boolean result = filter.test(model);

		// then
		assertTrue(result);
	}

	@Test
	public void shouldReturnTrueWhenAttributeDescriptorIsReturnedByComposedType()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		given(composedType.getUniqueKeyAttributes()).willReturn(Lists.newArrayList(model));
		given(model.getEnclosingType()).willReturn(composedType);
		given(model.getUnique()).willReturn(false);

		// when
		final boolean result = filter.test(model);

		// then
		assertTrue(result);
	}

}
