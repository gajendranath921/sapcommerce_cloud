/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;

import java.sql.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderHistoryPopulatorTest
{
	@Mock
	private PriceDataFactory priceDataFactory;

	private AbstractPopulatingConverter<OrderModel, OrderHistoryData> orderHistoryConverter;

	@Before
	public void setUp()
	{
		final OrderHistoryPopulator orderHistoryPopulator = new OrderHistoryPopulator();
		orderHistoryPopulator.setPriceDataFactory(priceDataFactory);
		orderHistoryConverter = new ConverterFactory<OrderModel, OrderHistoryData, OrderHistoryPopulator>().create(
				OrderHistoryData.class, orderHistoryPopulator);
	}

	@Test
	public void testConvert()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		final Date date = mock(Date.class);
		final OrderStatus orderStatus = mock(OrderStatus.class);
		given(orderModel.getCode()).willReturn("code");
		given(orderModel.getDate()).willReturn(date);
		given(orderModel.getStatus()).willReturn(orderStatus);
		given(orderModel.getTotalPrice()).willReturn(Double.valueOf(123.0));
		final OrderHistoryData orderHistoryData = orderHistoryConverter.convert(orderModel);
		Assert.assertEquals("code", orderHistoryData.getCode());
		Assert.assertEquals(date, orderHistoryData.getPlaced());
		Assert.assertEquals(orderStatus, orderHistoryData.getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertNull()
	{
		orderHistoryConverter.convert(null);
	}
}
