/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.search.solrfacetsearch.provider.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.util.Collection;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * Test to check if ProductBundleTemplatesValueProvider returns bundle templates associated with product
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductBundleTemplatesValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final ProductModel product = new ProductModel();
	@Mock
	private IndexedProperty indexedProperty;

	@Mock
	private BundleTemplateModel bundle1, bundle2;

	@Before
	public void setUp()
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return "bundleTemplate";
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new ProductBundleTemplatesValueProvider());
		configureBase();

		((ProductBundleTemplatesValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		product.setBundleTemplates(createBundles());
		Mockito.lenient().when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(Lists.newArrayList(getPropertyName()));

	}


	@Test
	public void testGetFiledValues() throws FieldValueProviderException
	{
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, product);

		Assert.assertTrue(!result.isEmpty());
		Assert.assertEquals(2, result.size());
		final Set<String> expectedResult = Sets.newHashSet("b1", "b2");
		for (final FieldValue fieldValue : result)
		{
			Assert.assertTrue(expectedResult.contains(String.valueOf(fieldValue.getValue())));
		}
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null)).size();
	}

	private Collection<BundleTemplateModel> createBundles()
	{


		Mockito.lenient().when(bundle1.getId()).thenReturn("b1");
		Mockito.lenient().when(bundle1.getVersion()).thenReturn("1.0");
		Mockito.lenient().when(bundle2.getId()).thenReturn("b2");
		Mockito.lenient().when(bundle2.getVersion()).thenReturn("1.0");
		return Lists.newArrayList(bundle1, bundle2);

	}

}
