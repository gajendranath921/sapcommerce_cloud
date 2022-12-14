/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.price.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultCommercePriceService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommercePriceServiceTest
{
	private static final String TEST_CURRENCY = "EUR";
	private static final Boolean TEST_NET = Boolean.FALSE;

	private DefaultCommercePriceService csPriceService;
	@Mock
	private PriceService priceService;
	@Mock
	private ProductModel product;
	@Mock
	private VariantProductModel var1;
	@Mock
	private VariantProductModel var2;
	@Mock
	private PriceInformation priceInfo;
	@Mock
	private PriceInformation varPriceInfo;
	@Mock(lenient = true)
	private PriceValue priceVal;
	@Mock(lenient = true)
	private PriceValue varPriceVal;


	@Before
	public void setUp() throws Exception
	{
		csPriceService = new DefaultCommercePriceService();
		csPriceService.setPriceService(priceService);
		given(priceInfo.getPriceValue()).willReturn(priceVal);
		given(varPriceInfo.getPriceValue()).willReturn(varPriceVal);
		given(priceVal.getCurrencyIso()).willReturn(TEST_CURRENCY);
		given(Boolean.valueOf(priceVal.isNet())).willReturn(TEST_NET);
		given(varPriceVal.getCurrencyIso()).willReturn(TEST_CURRENCY);
		given(Boolean.valueOf(varPriceVal.isNet())).willReturn(TEST_NET);
	}

	@Test
	public void testFromPriceVariants()
	{
		final List<PriceInformation> listPriceInfo = new ArrayList<PriceInformation>();
		final List<PriceInformation> varListPriceInfo = new ArrayList<PriceInformation>();
		final List<VariantProductModel> list = new LinkedList<VariantProductModel>();
		list.add(var1);
		list.add(var2);
		given(priceService.getPriceInformationsForProduct(var1)).willReturn(listPriceInfo);
		given(priceService.getPriceInformationsForProduct(var2)).willReturn(varListPriceInfo);
		given(product.getVariants()).willReturn(list);
		listPriceInfo.add(priceInfo);
		varListPriceInfo.add(varPriceInfo);
		given(Double.valueOf(priceVal.getValue())).willReturn(new Double(1d));
		given(Double.valueOf(varPriceVal.getValue())).willReturn(new Double(2d));
		Assert.assertEquals(priceInfo, csPriceService.getFromPriceForProduct(product));
		given(Double.valueOf(priceVal.getValue())).willReturn(new Double(2d));
		given(Double.valueOf(varPriceVal.getValue())).willReturn(new Double(1d));
		Assert.assertEquals(varPriceInfo, csPriceService.getFromPriceForProduct(product));
		given(Double.valueOf(priceVal.getValue())).willReturn(new Double(1d));
		Assert.assertEquals(priceInfo, csPriceService.getFromPriceForProduct(product));
	}

	@Test
	public void testFromPriceBase()
	{
		final List<PriceInformation> listPriceInfo = new ArrayList<PriceInformation>();
		given(product.getVariants()).willReturn(Collections.EMPTY_LIST);
		given(priceService.getPriceInformationsForProduct(product)).willReturn(listPriceInfo);
		listPriceInfo.add(priceInfo);
		Assert.assertEquals(priceInfo, csPriceService.getFromPriceForProduct(product));
	}

	@Test
	public void testWebPrice()
	{
		final List<PriceInformation> listPriceInfo = new ArrayList<PriceInformation>();
		given(priceService.getPriceInformationsForProduct(product)).willReturn(listPriceInfo);
		listPriceInfo.add(priceInfo);

		Assert.assertEquals(priceInfo, csPriceService.getWebPriceForProduct(product));
	}
}
