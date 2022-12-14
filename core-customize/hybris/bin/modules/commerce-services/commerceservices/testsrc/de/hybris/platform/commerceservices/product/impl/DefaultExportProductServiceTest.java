/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.product.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultExportProductServiceTest
{

	private DefaultExportProductService defaultExportProductService;

	@Mock
	private PagedFlexibleSearchService pagedFlexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		defaultExportProductService = new DefaultExportProductService();
		defaultExportProductService.setPagedFlexibleSearchService(pagedFlexibleSearchService);
	}

	@Test
	public void testGetModifiedProducts()
	{
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final Date timestamp = new Date();

		final SearchPageData<ProductModel> result = new SearchPageData();
		final ProductModel product = mock(ProductModel.class);
		result.setResults(Arrays.asList(product, product, product, product));
		final PaginationData pagination = new PaginationData();
		result.setPagination(pagination);
		final String query = "SELECT {pk} FROM {Product} WHERE {catalogVersion} IN (?catalogVersions) AND {modifiedtime} > ?modifiedTime";

		given(pagedFlexibleSearchService.search(Mockito.eq(query), Mockito.nullable(Map.class), Mockito.nullable(PageableData.class))).willReturn(result);

		final SearchPageData<ProductModel> modifiedProducts = defaultExportProductService.getModifiedProducts(
				Collections.singletonList(catalogVersion), timestamp, 0, 10);

		assertThat(modifiedProducts.getResults()).hasSize(4);
		assertThat(modifiedProducts.getPagination()).isSameAs(pagination);
	}
}
