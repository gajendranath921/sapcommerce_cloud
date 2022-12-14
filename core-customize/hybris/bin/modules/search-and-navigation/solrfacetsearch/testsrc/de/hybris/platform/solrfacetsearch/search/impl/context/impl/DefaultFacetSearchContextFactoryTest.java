/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.search.impl.context.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.common.ListenersFactory;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext.Status;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener;
import de.hybris.platform.solrfacetsearch.search.context.impl.DefaultFacetSearchContextFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFacetSearchContextFactoryTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SessionService sessionService;

	@Mock
	private JaloSession rawSession;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private ListenersFactory listenersFactory;

	@Mock
	private FacetSearchListener listener1;

	@Mock
	private FacetSearchListener listener2;

	private FacetSearchConfig facetSearchConfig;
	private IndexedType indexedType;
	private SearchQuery searchQuery;

	private DefaultFacetSearchContextFactory facetSearchContextFactory;

	@Before
	public void setUp()
	{
		when(sessionService.getRawSession(nullable(Session.class))).thenReturn(rawSession);

		facetSearchConfig = new FacetSearchConfig();
		indexedType = new IndexedType();
		searchQuery = new SearchQuery(facetSearchConfig, indexedType);

		facetSearchContextFactory = new DefaultFacetSearchContextFactory();
		facetSearchContextFactory.setSessionService(sessionService);
		facetSearchContextFactory.setCatalogVersionService(catalogVersionService);
		facetSearchContextFactory.setListenersFactory(listenersFactory);
	}

	@Test
	public void createContext() throws Exception
	{
		// when
		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// then
		assertNotNull(facetSearchContext);
		assertEquals(Status.CREATED, facetSearchContext.getStatus());
	}

	@Test
	public void initializeContext() throws Exception
	{
		// given
		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.initializeContext();

		// then
		assertEquals(Status.EXECUTING, facetSearchContext.getStatus());
	}

	@Test
	public void doesNotHaveCurrentContext() throws Exception
	{
		// expect
		expectedException.expect(IllegalStateException.class);

		// when
		facetSearchContextFactory.getContext();
	}

	@Test
	public void hasCurrentContext() throws Exception
	{
		// given
		final FacetSearchContext expectedFacetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig,
				indexedType, searchQuery);

		// when
		final FacetSearchContext facetSearchContext = facetSearchContextFactory.getContext();

		// then
		assertNotNull(facetSearchContext);
		assertSame(expectedFacetSearchContext, facetSearchContext);
	}

	@Test
	public void destroyContext() throws Exception
	{
		// given
		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.destroyContext();

		// then
		assertEquals(Status.COMPLETED, facetSearchContext.getStatus());
	}

	@Test
	public void destroyContextAfterException() throws Exception
	{
		// given
		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.destroyContext(new RuntimeException());

		// then
		assertEquals(Status.FAILED, facetSearchContext.getStatus());
	}

	@Test
	public void noListenerConfigured() throws Exception
	{
		// given
		final List<FacetSearchListener> listeners = Collections.emptyList();

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, FacetSearchListener.class)).thenReturn(listeners);

		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.initializeContext();
		facetSearchContextFactory.destroyContext();

		// then
		verify(listener1, never()).beforeSearch(facetSearchContext);
		verify(listener2, never()).beforeSearch(facetSearchContext);
		verify(listener2, never()).beforeSearch(facetSearchContext);
		verify(listener1, never()).beforeSearch(facetSearchContext);
	}

	@Test
	public void runBeforeBatchListener() throws Exception
	{
		// given
		final List<FacetSearchListener> listeners = Arrays.asList(listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, FacetSearchListener.class)).thenReturn(listeners);

		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.initializeContext();

		// then
		final InOrder inOrder = inOrder(listener1);
		inOrder.verify(listener1).beforeSearch(facetSearchContext);
		inOrder.verify(listener1, never()).afterSearch(facetSearchContext);
	}

	@Test
	public void runListeners() throws Exception
	{
		// given
		final List<FacetSearchListener> listeners = Arrays.asList(listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, FacetSearchListener.class)).thenReturn(listeners);

		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.initializeContext();
		facetSearchContextFactory.destroyContext();

		// then
		final InOrder inOrder = inOrder(listener1);
		inOrder.verify(listener1).beforeSearch(facetSearchContext);
		inOrder.verify(listener1).afterSearch(facetSearchContext);
	}

	@Test
	public void runAfterBatchErrorListener() throws Exception
	{
		// given
		final List<FacetSearchListener> listeners = Arrays.asList(listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, FacetSearchListener.class)).thenReturn(listeners);

		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.initializeContext();
		facetSearchContextFactory.destroyContext(new Exception());

		// then
		final InOrder inOrder = inOrder(listener1);
		inOrder.verify(listener1).beforeSearch(facetSearchContext);
		inOrder.verify(listener1).afterSearchError(facetSearchContext);
	}

	@Test
	public void listenersRunInCorrectOrder() throws Exception
	{
		// given
		final List<FacetSearchListener> listeners = Arrays.asList(listener2, listener1);

		when(listenersFactory.getListeners(facetSearchConfig, indexedType, FacetSearchListener.class)).thenReturn(listeners);

		final FacetSearchContext facetSearchContext = facetSearchContextFactory.createContext(facetSearchConfig, indexedType,
				searchQuery);

		// when
		facetSearchContextFactory.initializeContext();
		facetSearchContextFactory.destroyContext();

		// then
		final InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener2).beforeSearch(facetSearchContext);
		inOrder.verify(listener1).beforeSearch(facetSearchContext);
		inOrder.verify(listener1).afterSearch(facetSearchContext);
		inOrder.verify(listener2).afterSearch(facetSearchContext);
	}
}
