/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;



/**
 * Test of {@link DefaultOrderQuoteDiscountValuesAccessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderQuoteDiscountValuesAccessorTest
{
	@InjectMocks
	private DefaultOrderQuoteDiscountValuesAccessor defaultOrderQuoteDiscountValuesAccessor;

	@Mock
	private AbstractOrderModel order;

	@Test
	public void shouldGetQuoteDiscountValues()
	{
		when(order.getQuoteDiscountValuesInternal()).thenReturn("[<DV<QuoteDiscount#20.0#false#28.8#USD#false>VD>]");
		final List<DiscountValue> quoteDiscountValues = defaultOrderQuoteDiscountValuesAccessor.getQuoteDiscountValues(order);
		Assert.assertEquals("Unexpected number of quote discount values", 1, quoteDiscountValues.size());
		Assert.assertEquals("Unexpected discount code", "QuoteDiscount", quoteDiscountValues.get(0).getCode());
		Assert.assertEquals("Discount is absolute", Boolean.FALSE, Boolean.valueOf(quoteDiscountValues.get(0).isAbsolute()));
		Assert.assertEquals("Discount is tartget price", Boolean.FALSE,
				Boolean.valueOf(quoteDiscountValues.get(0).isAsTargetPrice()));
		Assert.assertEquals("Unexpected discount value", 20.0d, quoteDiscountValues.get(0).getValue(), 0.01d);
		Assert.assertEquals("Unexpected discount value", 28.8d, quoteDiscountValues.get(0).getAppliedValue(), 0.01d);
		Assert.assertEquals("Unexpected discount currency", "USD", quoteDiscountValues.get(0).getCurrencyIsoCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetQuoteDiscountValuesOrderNull()
	{
		defaultOrderQuoteDiscountValuesAccessor.getQuoteDiscountValues(null);
	}

	@Test
	public void shouldSetQuoteDiscountValues()
	{
		defaultOrderQuoteDiscountValuesAccessor.setQuoteDiscountValues(order, createDiscountValues());
		verify(order).setQuoteDiscountValuesInternal(anyString());
		verify(order).setCalculated(Boolean.FALSE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSetQuoteDiscountValuesOrderNull()
	{
		defaultOrderQuoteDiscountValuesAccessor.setQuoteDiscountValues(null, createDiscountValues());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotSetQuoteDiscountValuesDiscountValuesNull()
	{
		defaultOrderQuoteDiscountValuesAccessor.setQuoteDiscountValues(order, null);
	}

	protected List<DiscountValue> createDiscountValues()
	{
		return new ArrayList<>();
	}

}
