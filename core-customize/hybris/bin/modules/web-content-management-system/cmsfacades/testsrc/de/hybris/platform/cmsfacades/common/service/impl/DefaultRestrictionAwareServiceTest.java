/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.rendering.RestrictionContextProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultRestrictionAwareServiceTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String TEST_STRING = "this is a sample string";

	@Mock
	private RestrictionData restrictionData;

	@Mock
	private RestrictionContextProvider restrictionContextProvider;

	@InjectMocks
	private DefaultRestrictionAwareService restrictionAwareService;

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void whenExecuted_RestrictionIsStored_ThenSupplierIsExecuted_AndRestrictionIsCleaned()
	{
		// GIVEN
		verify(restrictionContextProvider, never()).setRestrictionInContext(any());
		verify(restrictionContextProvider, never()).removeRestrictionFromContext();

		// WHEN
		String result = restrictionAwareService.execute(restrictionData, this::sampleMethod);

		// THEN
		verify(restrictionContextProvider, times(1)).setRestrictionInContext(restrictionData);
		verify(restrictionContextProvider, times(1)).removeRestrictionFromContext();
		assertThat(result, is(TEST_STRING));
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	public String sampleMethod()
	{
		return TEST_STRING;
	}
}
