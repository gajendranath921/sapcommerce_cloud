/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.indexer.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.solrfacetsearch.config.CommitMode;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider.CommitType;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CommitModeListenerTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private Index index;

	@Mock
	private IndexerContext indexerContext;

	@Mock
	private IndexerBatchContext indexerBatchContext;

	@Mock
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Mock
	private SolrSearchProvider solrSearchProvider;

	private IndexConfig indexConfig;
	private CommitModeListener commitModeListener;

	@Before
	public void setUp() throws SolrServiceException
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final List<PK> pks = Collections.singletonList(PK.NULL_PK);
		final Map<String, String> indexerHints = new HashMap<String, String>();

		indexConfig = new IndexConfig();
		facetSearchConfig.setIndexConfig(indexConfig);

		when(indexerContext.getIndex()).thenReturn(index);
		when(indexerContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
		when(indexerContext.getIndexedType()).thenReturn(indexedType);
		when(indexerContext.getPks()).thenReturn(pks);
		when(indexerContext.getIndexerHints()).thenReturn(indexerHints);

		when(indexerBatchContext.getIndex()).thenReturn(index);
		when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
		when(indexerBatchContext.getIndexedType()).thenReturn(indexedType);
		when(indexerBatchContext.getPks()).thenReturn(pks);
		when(indexerBatchContext.getIndexerHints()).thenReturn(indexerHints);

		when(solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType)).thenReturn(solrSearchProvider);

		commitModeListener = new CommitModeListener();
		commitModeListener.setSolrSearchProviderFactory(solrSearchProviderFactory);
	}

	private void runListeners() throws Exception
	{
		commitModeListener.beforeIndex(indexerContext);
		commitModeListener.beforeBatch(indexerBatchContext);
		commitModeListener.afterBatch(indexerBatchContext);
		commitModeListener.beforeBatch(indexerBatchContext);
		commitModeListener.afterBatch(indexerBatchContext);
		commitModeListener.afterIndex(indexerContext);
	}

	@Test
	public void shouldNeverCommit() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.NEVER);

		// when
		runListeners();

		// then
		verify(solrSearchProvider, never()).commit(any(Index.class), any(CommitType.class));
	}

	@Test
	public void shouldCommitAfterIndex() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.AFTER_INDEX);

		// when
		runListeners();

		// then
		verify(solrSearchProvider, times(1)).commit(indexerContext.getIndex(), CommitType.HARD);
	}

	@Test
	public void shouldCommitAfterBatch() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.AFTER_BATCH);

		// when
		runListeners();

		// then
		verify(solrSearchProvider, times(2)).commit(indexerBatchContext.getIndex(), CommitType.HARD);
	}

	@Test
	public void shouldCommitMixed() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.MIXED);

		// when
		runListeners();

		// then
		final InOrder inOrder = inOrder(solrSearchProvider);
		inOrder.verify(solrSearchProvider, times(2)).commit(indexerBatchContext.getIndex(), CommitType.SOFT);
		inOrder.verify(solrSearchProvider, times(1)).commit(indexerContext.getIndex(), CommitType.HARD);
	}

	@Test
	public void defaultModeShouldBeCommitAfterIndex() throws Exception
	{
		// given
		indexConfig.setCommitMode(null);

		// when
		runListeners();

		// then
		verify(solrSearchProvider, times(1)).commit(indexerContext.getIndex(), CommitType.HARD);
	}

	@Test
	public void shouldNeverCommitOnBeforeAndErrorListeners() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.MIXED);

		// when
		commitModeListener.beforeIndex(indexerContext);
		commitModeListener.beforeBatch(indexerBatchContext);
		commitModeListener.afterBatchError(indexerBatchContext);
		commitModeListener.afterIndexError(indexerContext);

		// then
		verify(solrSearchProvider, never()).commit(any(Index.class), any(CommitType.class));
	}

	@Test
	public void indexerHintShouldBeCommitModeMixed() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.NEVER);
		indexerContext.getIndexerHints().put(CommitModeListener.COMMIT_MODE_HINT, "MIXED");
		indexerBatchContext.getIndexerHints().put(CommitModeListener.COMMIT_MODE_HINT, "MIXED");

		// when
		runListeners();

		// then
		final InOrder inOrder = inOrder(solrSearchProvider);
		inOrder.verify(solrSearchProvider, times(2)).commit(indexerBatchContext.getIndex(), CommitType.SOFT);
		inOrder.verify(solrSearchProvider, times(1)).commit(indexerContext.getIndex(), CommitType.HARD);
	}

	@Test
	public void indexerHintIsInvalid() throws Exception
	{
		// given
		indexConfig.setCommitMode(CommitMode.NEVER);
		indexerContext.getIndexerHints().put(CommitModeListener.COMMIT_MODE_HINT, "mixed");
		indexerBatchContext.getIndexerHints().put(CommitModeListener.COMMIT_MODE_HINT, "mixed");

		// when
		runListeners();

		// then
		verify(solrSearchProvider, never()).commit(any(Index.class), any(CommitType.class));
	}
}
