/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartOfLinkToggleAttributePredicateTest
{
	@InjectMocks
	private PartOfLinkToggleAttributePredicate mainPredicate;

	@Mock
	private ComposedTypeContainsLinkTogglePredicate internalPredicate;

	@Mock
	private ComposedTypeModel composedTypeModel;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@Before
	public void setup()
	{
		mainPredicate.setCmsComposedTypeContainsLinkTogglePredicate(internalPredicate);
		when(attributeDescriptor.getDeclaringEnclosingType()).thenReturn(composedTypeModel);
	}

	@Test
	public void whenAttributeDescriptorIsExternalAndInternalPredicateIsTrue_shouldReturnTrue()
	{
		// GIVEN
		when(attributeDescriptor.getQualifier()).thenReturn(CmsfacadesConstants.FIELD_EXTERNAL_NAME);
		when(internalPredicate.test(composedTypeModel)).thenReturn(true);

		// WHEN
		boolean result = mainPredicate.test(attributeDescriptor);

		// THEN
		assertThat(result, is(true));
	}

	@Test
	public void whenAttributeDescriptorIsUrlLinkAndInternalPredicateIsTrue_shouldReturnTrue()
	{
		// GIVEN
		when(attributeDescriptor.getQualifier()).thenReturn(CmsfacadesConstants.FIELD_URL_LINK_NAME);
		when(internalPredicate.test(composedTypeModel)).thenReturn(true);

		// WHEN
		boolean result = mainPredicate.test(attributeDescriptor);

		// THEN
		assertThat(result, is(true));
	}

	@Test
	public void whenAttributeDescriptorNotContainsAnyOfExternalOrUrlLinkAndInternalPredicateIsTrue_shouldReturnFalse()
	{
		// GIVEN
		when(attributeDescriptor.getQualifier()).thenReturn("fakeField");
		when(internalPredicate.test(composedTypeModel)).thenReturn(true);

		// WHEN
		boolean result = mainPredicate.test(attributeDescriptor);

		// THEN
		assertThat(result, is(false));
	}
}
