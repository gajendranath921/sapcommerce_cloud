/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.version.predicate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionAttributeDescriptor;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class EnumTypePredicateTest
{
	private final String TYPE_CODE = "TYPE_CODE";

	@InjectMocks
	private EnumTypePredicate predicate;
	@Mock
	private TypeService typeService;
	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor;
	@Mock
	private TypeModel attributeType;

	@Before
	public void setup()
	{
		when(versionAttributeDescriptor.getType()).thenReturn(attributeType);
		when(typeService.isAssignableFrom(EnumerationValueModel._TYPECODE, TYPE_CODE)).thenReturn(true);
	}

	@Test
	public void givenAttributeOfTypeEnumThenPredicatesReturnsTrue()
	{
		// GIVEN
		when(attributeType.getCode()).thenReturn(TYPE_CODE);

		// WHEN
		final boolean result = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(result, equalTo(true));
	}

	@Test
	public void givenAttributeOfNotTypeEnumThenPredicatesReturnsFalse()
	{
		// GIVEN
		when(attributeType.getCode()).thenReturn("ANOTHER_CODE");

		// WHEN
		final boolean result = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(result, equalTo(false));
	}
}
