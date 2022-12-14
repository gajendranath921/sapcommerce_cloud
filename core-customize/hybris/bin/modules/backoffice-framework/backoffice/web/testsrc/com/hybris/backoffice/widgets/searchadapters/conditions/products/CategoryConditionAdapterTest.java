/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.searchadapters.conditions.products;

import static com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData.ORPHANED_SEARCH_CONDITIONS_KEY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class CategoryConditionAdapterTest
{

	public static final String ALL_CATEGORIES = "allCategories";
	@Spy
	@InjectMocks
	private CategoryConditionAdapter categoryConditionAdapter;

	@Before
	public void setup()
	{
		categoryConditionAdapter.setCategoryPropertyName(ProductModel.SUPERCATEGORIES);
		categoryConditionAdapter.setCategoriesPropertyName(ALL_CATEGORIES);
		categoryConditionAdapter.setOperator(ValueComparisonOperator.CONTAINS);
		doReturn(false).when(categoryConditionAdapter).isReverseCategoryIndexLookupEnabled();
	}

	@Test
	public void shouldAddConditionsForAllCatalogs()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final CategoryModel category = mock(CategoryModel.class);
		final CategoryModel firstSubcategory = mock(CategoryModel.class);
		final CategoryModel secondSubcategory = mock(CategoryModel.class);
		final PK categoryPk = PK.fromLong(1L);
		final PK firstSubcategoryPk = PK.fromLong(2L);
		final PK secondSubcategoryPk = PK.fromLong(3L);

		given(navigationNode.getData()).willReturn(category);
		given(category.getAllSubcategories()).willReturn(Arrays.asList(firstSubcategory, secondSubcategory));
		given(category.getPk()).willReturn(categoryPk);
		given(firstSubcategory.getPk()).willReturn(firstSubcategoryPk);
		given(secondSubcategory.getPk()).willReturn(secondSubcategoryPk);

		// when
		categoryConditionAdapter.addSearchCondition(searchData, navigationNode);

		// then
		assertThat(searchData.getConditions(ORPHANED_SEARCH_CONDITIONS_KEY)).hasSize(1);
		final SearchConditionDataList searchConditionDataList = (SearchConditionDataList) searchData
				.getConditions(ORPHANED_SEARCH_CONDITIONS_KEY).get(0);
		assertThat(searchConditionDataList.getConditions()).onProperty("value").contains(firstSubcategoryPk, secondSubcategoryPk,
				categoryPk);
		assertThat(searchConditionDataList.getConditions()).onProperty("operator").contains(ValueComparisonOperator.CONTAINS,
				ValueComparisonOperator.CONTAINS, ValueComparisonOperator.CONTAINS);
	}

	@Test
	public void addSearchConditionShouldAddSingleConditionForReverseLookup()
	{
		//given
		doReturn(true).when(categoryConditionAdapter).isReverseCategoryIndexLookupEnabled();


		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final CategoryModel category = mock(CategoryModel.class);
		final PK pk = PK.fromLong(42);
		doReturn(pk).when(category).getPk();
		doReturn(category).when(navigationNode).getData();

		//when
		categoryConditionAdapter.addSearchCondition(searchData, navigationNode);

		//then
		assertThat(searchData.getSearchFields()).containsOnly(ALL_CATEGORIES);
		assertThat(searchData.getConditions(ALL_CATEGORIES)).hasSize(1);
		final SearchConditionData condition = searchData.getConditions(ALL_CATEGORIES).get(0);
		assertThat(condition.getOperator()).isEqualTo(ValueComparisonOperator.CONTAINS);
		assertThat(condition.getValue()).isEqualTo(pk);
	}
}
