/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.config.factories.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FlexibleSearchQuerySpec;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.provider.ContextAwareParameterProvider;
import de.hybris.platform.solrfacetsearch.provider.ParameterProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexOperationService;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFlexibleSearchQuerySpecFactoryTest
{
	private static final String PROVIDER_NAME = "providerName";
	private static final String QUERY = "Test query";
	private static final String PARAMETER_PROVIDER_ID = "TestParameterProvider";
	private static final String CONTEXT_AWARE_PARAMETER_PROVIDER_ID = "TestContextAwareProvider";
	private DefaultFlexibleSearchQuerySpecFactory factory;
	private IndexedTypeFlexibleSearchQuery indexTypeFlexibleSearchQueryData;

	@Mock
	private IndexedType indexedType;
	@Mock
	private FacetSearchConfig facetSearcConfig;
	@Mock
	private SolrIndexService indexService;
	@Mock
	private SolrIndexOperationService indexOperationService;
	@Mock
	private TimeService mockTimeService;
	@Mock
	private BeanFactory mockBeanFactory;
	private Date currentTime;
	private Date lastIndexTime;

	@Before
	public void setUp() throws SolrServiceException
	{
		factory = new DefaultFlexibleSearchQuerySpecFactory();
		indexTypeFlexibleSearchQueryData = new IndexedTypeFlexibleSearchQuery();

		lastIndexTime = new Date();
		currentTime = new Date();
		final SolrIndexModel index = new SolrIndexModel();
		factory.setIndexService(indexService);
		factory.setIndexOperationService(indexOperationService);
		factory.setTimeService(mockTimeService);
		factory.setBeanFactory(mockBeanFactory);
		when(indexService.getActiveIndex(facetSearcConfig.getName(), indexedType.getIdentifier())).thenReturn(index);
		when(indexOperationService.getLastIndexOperationTime(index)).thenReturn(lastIndexTime);
		when(mockTimeService.getCurrentTime()).thenReturn(currentTime);
		when(mockBeanFactory.getBean(PARAMETER_PROVIDER_ID)).thenReturn(new TestParameterProvider());
		when(mockBeanFactory.getBean(CONTEXT_AWARE_PARAMETER_PROVIDER_ID)).thenReturn(new TestContextAwareProvider());
	}

	@Test
	public void testCreate() throws SolrServiceException
	{
		final FlexibleSearchQuerySpec flexibleSearchQuerySpec = factory.createIndexQuery(indexTypeFlexibleSearchQueryData,
				indexedType, facetSearcConfig);
		Assert.assertNull(flexibleSearchQuerySpec.getQuery());
		assertThat(flexibleSearchQuerySpec.createParameters()).isEmpty();
	}

	@Test
	public void testCreateWithInjectedTimes() throws SolrServiceException
	{
		indexTypeFlexibleSearchQueryData.setInjectCurrentDate(true);
		indexTypeFlexibleSearchQueryData.setInjectCurrentTime(true);
		indexTypeFlexibleSearchQueryData.setInjectLastIndexTime(true);
		final FlexibleSearchQuerySpec flexibleSearchQuerySpec = factory.createIndexQuery(indexTypeFlexibleSearchQueryData,
				indexedType, facetSearcConfig);
		Assert.assertNull(flexibleSearchQuerySpec.getQuery());
		final Map<String, Object> queryParameters = flexibleSearchQuerySpec.createParameters();
		assertThat(queryParameters).hasSize(3);
		Assert.assertEquals(currentTime, queryParameters.get(DefaultFlexibleSearchQuerySpecFactory.CURRENTTIME));
		Assert.assertEquals(lastIndexTime, queryParameters.get(DefaultFlexibleSearchQuerySpecFactory.LASTINDEXTIME));
		Assert.assertNotNull(queryParameters.get(DefaultFlexibleSearchQuerySpecFactory.CURRENTDATE));
	}

	@Test
	public void testCreateWithInitialParametersAndDynamicProvider() throws SolrServiceException
	{

		final Map<String, Object> initialMap = new HashMap<String, Object>();
		initialMap.put("param", "value");
		indexTypeFlexibleSearchQueryData.setParameters(initialMap);
		indexTypeFlexibleSearchQueryData.setQuery(QUERY);
		indexTypeFlexibleSearchQueryData.setParameterProviderId(PARAMETER_PROVIDER_ID);

		final FlexibleSearchQuerySpec flexibleSearchQuerySpec = factory.createIndexQuery(indexTypeFlexibleSearchQueryData,
				indexedType, facetSearcConfig);
		Assert.assertEquals(QUERY, flexibleSearchQuerySpec.getQuery());
		final Map<String, Object> queryParameters = flexibleSearchQuerySpec.createParameters();
		assertThat(queryParameters).hasSize(2);
		Assert.assertEquals("value", queryParameters.get("param"));
		Assert.assertEquals(PARAMETER_PROVIDER_ID, queryParameters.get(PROVIDER_NAME));

	}

	@Test
	public void testCreateWithInitialParametersAndContextAwareProvider() throws SolrServiceException
	{

		final Map<String, Object> initialMap = new HashMap<String, Object>();
		initialMap.put("param1", "value1");

		indexTypeFlexibleSearchQueryData.setParameters(initialMap);
		indexTypeFlexibleSearchQueryData.setQuery(QUERY);
		indexTypeFlexibleSearchQueryData.setParameterProviderId(CONTEXT_AWARE_PARAMETER_PROVIDER_ID);

		final FlexibleSearchQuerySpec flexibleSearchQuerySpec = factory.createIndexQuery(indexTypeFlexibleSearchQueryData,
				indexedType, facetSearcConfig);
		Assert.assertEquals(QUERY, flexibleSearchQuerySpec.getQuery());
		final Map<String, Object> queryParameters = flexibleSearchQuerySpec.createParameters();
		assertThat(queryParameters).hasSize(2);
		Assert.assertEquals("value1", queryParameters.get("param1"));
		Assert.assertEquals(CONTEXT_AWARE_PARAMETER_PROVIDER_ID, queryParameters.get(PROVIDER_NAME));

	}

	class TestParameterProvider implements ParameterProvider
	{
		@Override
		public Map<String, Object> createParameters()
		{
			return Collections.<String, Object> singletonMap(PROVIDER_NAME, PARAMETER_PROVIDER_ID);
		}
	}

	class TestContextAwareProvider implements ContextAwareParameterProvider
	{
		@Override
		public Map<String, Object> createParameters(final IndexConfig indexConfig, final IndexedType indexedType)
		{
			return Collections.<String, Object> singletonMap(PROVIDER_NAME, CONTEXT_AWARE_PARAMETER_PROVIDER_ID);
		}
	}


}
