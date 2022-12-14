/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.provider.impl;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.variants.model.VariantProductModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShardAwareProductIdentityProviderTest
{
	private final ShardAwareProductIdentityProvider provider = new ShardAwareProductIdentityProvider();

	@Mock
	private ProductModel baseProduct;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private ProductModel product;
	@Mock
	private VariantProductModel baseVariantProduct;
	@Mock
	private VariantProductModel variantProduct;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogModel catalog;

	@Before
	public void setUp()
	{
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalogVersion.getVersion()).thenReturn("online");
		when(catalog.getId()).thenReturn("electronics");

		when(variantProduct.getCatalogVersion()).thenReturn(catalogVersion);
		when(baseVariantProduct.getCatalogVersion()).thenReturn(catalogVersion);
		when(baseProduct.getCatalogVersion()).thenReturn(catalogVersion);


		when(variantProduct.getBaseProduct()).thenReturn(baseVariantProduct);
		when(baseVariantProduct.getBaseProduct()).thenReturn(baseProduct);


		when(baseProduct.getCode()).thenReturn("baseProductCode");
		when(variantProduct.getCode()).thenReturn("variantProductCode");
		when(baseVariantProduct.getCode()).thenReturn("baseVariantProductCode");
	}

	@Test
	public void ProductWithOneBaseProductTest()
	{
		//given

		//when
		final String id = provider.getIdentifier(indexConfig, baseVariantProduct);

		//then
		final String[] idParts = id.split(ShardAwareProductIdentityProvider.GROUPING_SEPARATOR);

		assertEquals(2, idParts.length);
		assertEquals(baseProduct.getCode(), idParts[0]);
		assertTrue(idParts[1].contains(baseVariantProduct.getCode()));
	}

	@Test
	public void ProductWithMoreBaseProductTest()
	{
		//given

		//when
		final String id = provider.getIdentifier(indexConfig, variantProduct);

		//then
		final String[] idParts = id.split(ShardAwareProductIdentityProvider.GROUPING_SEPARATOR);

		assertEquals(2, idParts.length);
		assertEquals(baseProduct.getCode(), idParts[0]);
		assertTrue(idParts[1].contains(variantProduct.getCode()));
	}

	@Test
	public void BaseProductTest()
	{
		//given

		//when
		final String id = provider.getIdentifier(indexConfig, baseProduct);

		//then
		assertFalse(id.contains(ShardAwareProductIdentityProvider.GROUPING_SEPARATOR));
		assertTrue(id.contains(baseProduct.getCode()));
	}
}
