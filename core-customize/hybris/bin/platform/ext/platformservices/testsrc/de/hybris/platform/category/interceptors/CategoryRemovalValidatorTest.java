/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.category.interceptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test for {@link CategoryRemovalValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CategoryRemovalValidatorTest
{
	private static final String PARENT_CTG_CODE = "FooBar";
	@InjectMocks
	private final CategoryRemovalValidator categoryRemovalValidator = new CategoryRemovalValidator();
	@Mock
	private CategoryService categoryService;
	@Mock
	private CategoryModel parentCategory;
	@Mock
	private CategoryModel leafCategory;


	@Test
	public void shouldThrowInterceptorExceptionWhenCategoryHasSubcategories()
	{
		// given
		given(Boolean.valueOf(categoryService.isSubcategoryRemovalCheckDisabled())).willReturn(Boolean.FALSE);
		given(parentCategory.getAllSubcategories()).willReturn(Collections.singleton(leafCategory));
		given(parentCategory.getCode()).willReturn(PARENT_CTG_CODE);

		try
		{
			// when
			categoryRemovalValidator.onRemove(parentCategory, null);
			fail("should throw InterceptorException");
		}
		catch (final InterceptorException ie)
		{
			// then
			assertThat(ie.getMessage()).contains(
					"cannot remove [" + PARENT_CTG_CODE + "], since this category still has sub-categories");
		}
	}

	@Test
	public void shouldNotThrowInterceptorExceptionWhenCategoryIsLeaf()
	{
		// given
		given(Boolean.valueOf(categoryService.isSubcategoryRemovalCheckDisabled())).willReturn(Boolean.FALSE);
		given(leafCategory.getAllSubcategories()).willReturn(Collections.EMPTY_LIST);

		try
		{
			// when
			categoryRemovalValidator.onRemove(leafCategory, null);
		}
		catch (final InterceptorException e)
		{
			// then
			fail("should NOT throw InterceptorException");
		}
	}

	@Test
	public void shouldNotThrowInterceptorExceptionWhenCategoryHasSubcategoriesButSessionKeyIsTrue()
	{
		// given
		given(Boolean.valueOf(categoryService.isSubcategoryRemovalCheckDisabled())).willReturn(Boolean.TRUE);
		Mockito.lenient().when(parentCategory.getAllSubcategories()).thenReturn(Collections.singleton(leafCategory));

		try
		{
			// when
			categoryRemovalValidator.onRemove(parentCategory, null);
		}
		catch (final InterceptorException e)
		{
			// then
			fail("should NOT throw InterceptorException");
		}
	}
}
