/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AndFilterTest
{

	private final AndFilter<AttributeDescriptorModel> andFilter = new AndFilter<>();
	@Mock
	private UniqueCheckingFilter uniqueCheckingFilter;
	@Mock
	private DefaultValueCheckingFilter defaultValueCheckingFilter;
	final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);

	@Test
	public void shouldReturnTrueWhenAllSubFiltersReturnTrue()
	{
		// given
		given(uniqueCheckingFilter.test(model)).willReturn(true);
		given(defaultValueCheckingFilter.test(model)).willReturn(true);
		andFilter.setFilters(Arrays.asList(uniqueCheckingFilter, defaultValueCheckingFilter));

		// when
		final boolean result = andFilter.test(model);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenNotAllSubFiltersReturnTrue()
	{
		// given
		given(uniqueCheckingFilter.test(model)).willReturn(false);
		Mockito.lenient().when(defaultValueCheckingFilter.test(model)).thenReturn(true);
		andFilter.setFilters(Arrays.asList(uniqueCheckingFilter, defaultValueCheckingFilter));

		// when
		final boolean result = andFilter.test(model);

		// then
		assertThat(result).isFalse();
	}
}
