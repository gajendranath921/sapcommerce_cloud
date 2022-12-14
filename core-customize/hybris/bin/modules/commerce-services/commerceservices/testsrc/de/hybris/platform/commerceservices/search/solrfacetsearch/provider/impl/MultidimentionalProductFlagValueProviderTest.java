/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.MultidimentionalProductFlagValueProvider;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit Tests for {@link de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.MultidimentionalProductFlagValueProvider}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MultidimentionalProductFlagValueProviderTest extends PropertyFieldValueProviderTestBase
{
	private static final String SOLR_PROPERTY = "multidimensional";
	private static final String FIELD_NAME_INDEXING = SOLR_PROPERTY + "_string";
	private static final String FIELD_NAME_SORTING = SOLR_PROPERTY + "_sortable_string";

	@Mock
	private IndexedProperty indexedProperty;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return SOLR_PROPERTY;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new MultidimentionalProductFlagValueProvider());
		configureBase();

		((MultidimentionalProductFlagValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);
	}

	@Test
	public void testNoVariantsWillReturnEmptyList() throws FieldValueProviderException
	{
		final ProductModel model = new ProductModel();
		// model will have an empty list
		Mockito.when(modelService.getAttributeValue(Mockito.eq(model), Mockito.anyString())).thenReturn(Collections.emptyList());

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);

		//Expected Results (Collections.EMPTY_LIST). Assert we received valid results
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testValidVariantsWillReturnList() throws FieldValueProviderException
	{
		final ProductModel model = new ProductModel();
		final List<VariantProductModel> variants = new ArrayList<>();
		variants.add(new VariantProductModel());
		model.setVariants(variants);
		final List fieldNames = Arrays.asList(new String[]
		{ FIELD_NAME_INDEXING, FIELD_NAME_SORTING });

		Mockito.when(modelService.getAttributeValue(Mockito.eq(model), Mockito.nullable(String.class))).thenReturn(variants);
		Mockito.when(fieldNameProvider.getFieldNames(Mockito.eq(indexedProperty), Mockito.nullable(String.class))).thenReturn(fieldNames);

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);

		// result should not be empty
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
	}
}
