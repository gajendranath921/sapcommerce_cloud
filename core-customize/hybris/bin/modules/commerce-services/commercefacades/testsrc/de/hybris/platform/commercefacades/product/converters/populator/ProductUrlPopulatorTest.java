/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductUrlPopulatorTest
{
	private static final String PRODUCT_CODE = "proCode";
	private static final String PRODUCT_URL = "proURL/proCode";

	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;

	private final ProductUrlPopulator productUrlPopulator = new ProductUrlPopulator();
	private AbstractPopulatingConverter<ProductModel, ProductData> productUrlConverter;

	@Before
	public void setUp()
	{
		productUrlPopulator.setProductModelUrlResolver(productModelUrlResolver);

		productUrlConverter = new ConverterFactory<ProductModel, ProductData, ProductUrlPopulator>().create(ProductData.class,
				productUrlPopulator);
	}

	@Test
	public void testConvert()
	{
		final ProductModel source = mock(ProductModel.class);

		given(source.getCode()).willReturn(PRODUCT_CODE);
		given(productModelUrlResolver.resolve(any(ProductModel.class))).willReturn(PRODUCT_URL);

		final ProductData result = productUrlConverter.convert(source);

		Assert.assertEquals(PRODUCT_CODE, result.getCode());
		Assert.assertEquals(PRODUCT_URL, result.getUrl());
	}
}
