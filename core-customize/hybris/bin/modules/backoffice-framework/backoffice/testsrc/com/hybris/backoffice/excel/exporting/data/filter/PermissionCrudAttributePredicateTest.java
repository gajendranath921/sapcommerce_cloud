/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.exporting.data.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import org.mockito.Mockito;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PermissionCrudAttributePredicateTest
{
	@Mock
	PermissionCRUDService mockedPermissionCRUDService;
	@Spy
	@InjectMocks
	PermissionCrudAttributePredicate permissionCrudAttributePredicate;

	@Test
	public void shouldNotIncludeAttributesToWhichTheUserHasNoReadAccess()
	{
		// given
		final String qualifier = "price";
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getQualifier()).willReturn(qualifier);

		given(mockedPermissionCRUDService.canReadAttribute(attributeDescriptorModel)).willReturn(false);

		// when
		final boolean result = permissionCrudAttributePredicate.test(attributeDescriptorModel);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldIncludeAttributesToWhichTheUserHasReadAccess()
	{
		// given
		final String qualifier = "price";
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		Mockito.lenient().when(attributeDescriptorModel.getQualifier()).thenReturn(qualifier);

		given(mockedPermissionCRUDService.canReadAttribute(attributeDescriptorModel)).willReturn(true);

		// when
		final boolean result = permissionCrudAttributePredicate.test(attributeDescriptorModel);

		// then
		assertThat(result).isTrue();
	}
}
