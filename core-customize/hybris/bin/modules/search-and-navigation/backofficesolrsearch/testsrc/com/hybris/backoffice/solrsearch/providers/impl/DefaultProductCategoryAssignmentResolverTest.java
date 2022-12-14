/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProductCategoryAssignmentResolverTest
{

	@Spy
	private DefaultProductCategoryAssignmentResolver provider;

	@Test
	public void getIndexedCategoriesShouldReturnOwnAndBaseProductsCategoryAssignments()
	{
		//given
		final VariantProductModel variant = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);

		when(variant.getBaseProduct()).thenReturn(baseProduct);
		final CategoryModel baseCategory = mock(CategoryModel.class);
		final CategoryModel subCategory = mock(CategoryModel.class);
		when(baseProduct.getSupercategories()).thenReturn(Collections.singleton(baseCategory));
		when(variant.getSupercategories()).thenReturn(Collections.singleton(subCategory));

		//when
		final Set indexedCategories = provider.getIndexedCategories(variant);

		//then
		assertThat(indexedCategories).contains(baseCategory, subCategory);
	}
}
