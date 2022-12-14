/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class OrFilterTest
{
	private final OrFilter orFilter = new OrFilter();
	private final UniqueCheckingFilter uniqueCheckingFilter = new UniqueCheckingFilter();
	private final DefaultValueCheckingFilter defaultValueCheckingFilter = new DefaultValueCheckingFilter();

	@Before
	public void setUp()
	{
		orFilter.setExcelFilter1(uniqueCheckingFilter);
		orFilter.setExcelFilter2(defaultValueCheckingFilter);
	}

	@Test
	public void shouldResultBeOrOfGivenFilters()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		given(model.getUnique()).willReturn(true);
		given(model.getDefaultValue()).willReturn(null);

		// when
		final boolean uniqueResult = uniqueCheckingFilter.test(model);
		final boolean defaultValueResult = defaultValueCheckingFilter.test(model);
		final boolean orFilterResult = orFilter.test(model);

		// then
		assertTrue(uniqueResult);
		assertFalse(defaultValueResult);
		assertTrue(orFilterResult);
	}
}
