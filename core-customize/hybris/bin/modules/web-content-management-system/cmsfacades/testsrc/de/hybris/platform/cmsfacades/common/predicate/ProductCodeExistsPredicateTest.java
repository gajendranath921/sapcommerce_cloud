/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProductCodeExistsPredicateTest
{
	@InjectMocks
	private ProductCodeExistsPredicate predicate;

	@Mock
	private ProductService productService;

	@Mock
	private ProductModel productModel;

	private String VALID_PRODUCT_CODE = "validProductCode";
	private String INVALID_PRODUCT_CODE = "invalidProductCode";


	@Test
	public void shouldReturnTrueIfProductCodeExists()
	{

		// GIVEN
		when(productService.getProductForCode(VALID_PRODUCT_CODE)).thenReturn(productModel);

		// WHEN
		boolean result = predicate.test(VALID_PRODUCT_CODE);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfProductCodeNotExists()
	{
		// GIVEN
		when(productService.getProductForCode(INVALID_PRODUCT_CODE)).thenThrow(new RuntimeException(""));

		// WHEN
		boolean result = predicate.test(INVALID_PRODUCT_CODE);

		// THEN
		assertFalse(result);
	}
}
