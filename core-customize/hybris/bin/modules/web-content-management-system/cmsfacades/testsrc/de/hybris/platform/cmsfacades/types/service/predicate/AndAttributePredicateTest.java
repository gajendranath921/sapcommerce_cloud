/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class AndAttributePredicateTest
{
    @Mock
    private AttributeDescriptorModel attributeDescriptor;

    @Mock
    private Predicate<AttributeDescriptorModel> predicate1;

    @Mock
    private Predicate<AttributeDescriptorModel> predicate2;

    @InjectMocks
    private AndAttributePredicate andAttributePredicate;

    @Before
    public void setUp()
    {
        andAttributePredicate.setPredicates(Arrays.asList(predicate1, predicate2));
    }

    @Test
    public void givenAllPredicatesPass_WhenAndPredicateIsTested_ThenItReturnsTrue()
    {
        // GIVEN
        when(predicate1.test(attributeDescriptor)).thenReturn(true);
        when(predicate2.test(attributeDescriptor)).thenReturn(true);

        // WHEN
        boolean result = andAttributePredicate.test(attributeDescriptor);

        // THEN
        assertThat(result, is(true));
    }

    @Test
    public void givenNoPredicatePass_WhenAndPredicateIsTested_ThenItReturnsFalse()
    {
        // GIVEN
        when(predicate1.test(attributeDescriptor)).thenReturn(false);
        when(predicate2.test(attributeDescriptor)).thenReturn(false);

        // WHEN
        boolean result = andAttributePredicate.test(attributeDescriptor);

        // THEN
        assertThat(result, is(false));
    }

    @Test
    public void givenOnePredicateFails_WhenAndPredicateIsTested_ThenItReturnsFalse()
    {
        // GIVEN
        when(predicate1.test(attributeDescriptor)).thenReturn(true);
        when(predicate2.test(attributeDescriptor)).thenReturn(false);

        // WHEN
        boolean result = andAttributePredicate.test(attributeDescriptor);

        // THEN
        assertThat(result, is(false));
    }
}
