/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearchbackoffice.editors.excludedsorts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsSortConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedSort;
import de.hybris.platform.adaptivesearch.data.AsSearchResultData;
import de.hybris.platform.adaptivesearch.model.AsExcludedSortModel;
import de.hybris.platform.adaptivesearch.strategies.impl.DefaultMergeMap;
import de.hybris.platform.adaptivesearchbackoffice.data.ExcludedSortEditorData;
import de.hybris.platform.adaptivesearchbackoffice.data.SearchResultData;
import de.hybris.platform.adaptivesearchbackoffice.editors.configurablemultireference.AbstractDataHandlerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zul.ListModel;


@UnitTest
public class AsExcludedSortsDataHandlerTest extends AbstractDataHandlerTest
{
	@Mock
	private I18NService i18NService;

	@InjectMocks
	private AsExcludedSortsDataHandler asExcludedSortsDataHandler;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		Mockito.when(i18NService.getSupportedLocales()).thenReturn(Set.of(Locale.ENGLISH));
	}

	@Test
	public void getTypeCode() throws Exception
	{
		// when
		final String type = asExcludedSortsDataHandler.getTypeCode();

		// then
		assertEquals(AsExcludedSortModel._TYPECODE, type);
	}

	@Test
	public void loadNullData()
	{
		// when
		final ListModel<ExcludedSortEditorData> listModel = asExcludedSortsDataHandler.loadData(null, null, null);

		// then
		assertEquals(0, listModel.getSize());
	}

	@Test
	public void loadEmptyData()
	{
		// given
		final SearchResultData searchResult = new SearchResultData();
		searchResult.setAsSearchResult(new AsSearchResultData());

		// when
		final ListModel<ExcludedSortEditorData> listModel = asExcludedSortsDataHandler.loadData(new ArrayList<>(), searchResult,
				new HashMap<>());

		// then
		assertEquals(0, listModel.getSize());
	}

	@Test
	public void loadDataFromInitialValue() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSortModel1 = createExcludedSortModel1();
		final AsExcludedSortModel excludedSortModel2 = createExcludedSortModel2();
		final List<AsExcludedSortModel> initialValue = Arrays.asList(excludedSortModel1, excludedSortModel2);

		// when
		final ListModel<ExcludedSortEditorData> listModel = asExcludedSortsDataHandler.loadData(initialValue, null,
				new HashMap<>());

		// then
		assertEquals(2, listModel.getSize());

		final ExcludedSortEditorData excludedSort1 = listModel.getElementAt(0);
		assertEquals(UID_1, excludedSort1.getUid());
		assertEquals(CODE_1, excludedSort1.getCode());
		//	assertEquals(PRIORITY_1, excludedSort1.getPriority());
		assertFalse(excludedSort1.isOpen());
		assertFalse(excludedSort1.isOverride());
		assertFalse(excludedSort1.isInSearchResult());
		assertSame(excludedSortModel1, excludedSort1.getModel());

		final ExcludedSortEditorData excludedSort2 = listModel.getElementAt(1);
		assertEquals(UID_2, excludedSort2.getUid());
		assertEquals(CODE_2, excludedSort2.getCode());
		//	assertEquals(PRIORITY_2, excludedSort2.getPriority());
		assertFalse(excludedSort2.isOpen());
		assertFalse(excludedSort2.isOverride());
		assertFalse(excludedSort2.isInSearchResult());
		assertSame(excludedSortModel2, excludedSort2.getModel());
	}

	@Test
	public void loadDataFromSearchResult() throws Exception
	{
		// given
		final Map<String, AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>> searchProfileResultExcludedSorts = new DefaultMergeMap<>();
		searchProfileResultExcludedSorts.put(CODE_1, createConfigurationHolder(createExcludedSortData1()));
		searchProfileResultExcludedSorts.put(CODE_2, createConfigurationHolder(createExcludedSortData2()));

		final SearchResultData searchResult = createSearchResult();
		final AsSearchResultData asSearchResult = searchResult.getAsSearchResult();
		asSearchResult.getSearchProfileResult().setExcludedSorts(searchProfileResultExcludedSorts);

		// when
		final ListModel<ExcludedSortEditorData> listModel = asExcludedSortsDataHandler.loadData(null, searchResult,
				new HashMap<>());

		// then
		assertEquals(2, listModel.getSize());

		final ExcludedSortEditorData excludedSort1 = listModel.getElementAt(0);
		assertEquals(UID_1, excludedSort1.getUid());
		assertEquals(CODE_1, excludedSort1.getCode());
		assertEquals(PRIORITY_1, excludedSort1.getPriority());
		assertFalse(excludedSort1.isOpen());
		assertFalse(excludedSort1.isOverride());
		assertNull(excludedSort1.getModel());

		final ExcludedSortEditorData excludedSort2 = listModel.getElementAt(1);
		assertEquals(UID_2, excludedSort2.getUid());
		assertEquals(CODE_2, excludedSort2.getCode());
		assertEquals(PRIORITY_2, excludedSort2.getPriority());
		assertFalse(excludedSort2.isOpen());
		assertFalse(excludedSort2.isOverride());
		assertFalse(excludedSort2.isInSearchResult());
		assertNull(excludedSort2.getModel());
	}

	@Test
	public void loadDataCombined() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSortModel = createExcludedSortModel1();
		final List<AsExcludedSortModel> initialValue = Collections.singletonList(excludedSortModel);

		final Map<String, AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>> searchProfileResultExcludedSorts = new DefaultMergeMap<>();
		searchProfileResultExcludedSorts.put(CODE_1, createConfigurationHolder(createExcludedSortData1()));
		searchProfileResultExcludedSorts.put(CODE_2, createConfigurationHolder(createExcludedSortData2()));

		final SearchResultData searchResult = createSearchResult();
		final AsSearchResultData asSearchResult = searchResult.getAsSearchResult();
		asSearchResult.getSearchProfileResult().setExcludedSorts(searchProfileResultExcludedSorts);

		// when
		final ListModel<ExcludedSortEditorData> listModel = asExcludedSortsDataHandler.loadData(initialValue, searchResult,
				new HashMap<>());

		// then
		assertEquals(2, listModel.getSize());

		final ExcludedSortEditorData excludedSort1 = listModel.getElementAt(0);
		assertEquals(UID_1, excludedSort1.getUid());
		assertEquals(CODE_1, excludedSort1.getCode());
		assertEquals(PRIORITY_1, excludedSort1.getPriority());
		assertFalse(excludedSort1.isOpen());
		assertFalse(excludedSort1.isOverride());
		assertFalse(excludedSort1.isInSearchResult());
		assertSame(excludedSortModel, excludedSort1.getModel());

		final ExcludedSortEditorData excludedSort2 = listModel.getElementAt(1);
		assertEquals(UID_2, excludedSort2.getUid());
		assertEquals(CODE_2, excludedSort2.getCode());
		assertEquals(PRIORITY_2, excludedSort2.getPriority());
		assertFalse(excludedSort2.isOpen());
		assertFalse(excludedSort2.isOverride());
		assertFalse(excludedSort2.isInSearchResult());
		assertNull(excludedSort2.getModel());
	}

	@Test
	public void getValue() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSortModel = createExcludedSortModel1();
		final List<AsExcludedSortModel> initialValue = Collections.singletonList(excludedSortModel);

		final ListModel<ExcludedSortEditorData> model = asExcludedSortsDataHandler.loadData(initialValue, null, new HashMap<>());

		// when
		final List<AsExcludedSortModel> value = asExcludedSortsDataHandler.getValue(model);

		// then
		assertNotNull(value);
		assertEquals(1, value.size());
		assertSame(excludedSortModel, value.get(0));
	}

	@Test
	public void getItemValue() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSortModel = createExcludedSortModel1();
		final List<AsExcludedSortModel> initialValue = Collections.singletonList(excludedSortModel);

		final ListModel<ExcludedSortEditorData> model = asExcludedSortsDataHandler.loadData(initialValue, null, new HashMap<>());

		// when
		final AsExcludedSortModel itemValue = asExcludedSortsDataHandler.getItemValue(model.getElementAt(0));

		// then
		assertSame(excludedSortModel, itemValue);
	}

	@Test
	public void getAttributeValue() throws Exception
	{
		// given
		final AsExcludedSortModel excludedSortModel = createExcludedSortModel1();
		final List<AsExcludedSortModel> initialValue = Collections.singletonList(excludedSortModel);

		final ListModel<ExcludedSortEditorData> listModel = asExcludedSortsDataHandler.loadData(initialValue, null,
				new HashMap<>());
		final ExcludedSortEditorData excludedSort = listModel.getElementAt(0);

		// when
		final Object priority = asExcludedSortsDataHandler.getAttributeValue(excludedSort, PRIORITY_ATTRIBUTE);

		// then
		assertEquals(PRIORITY_1, priority);
	}

	protected AsExcludedSortModel createExcludedSortModel1()
	{
		final AsExcludedSortModel excludedSortModel = new AsExcludedSortModel();
		excludedSortModel.setUid(UID_1);
		excludedSortModel.setCode(CODE_1);
		excludedSortModel.setPriority(PRIORITY_1);
		excludedSortModel.setName(NAME_1, LOCALE);

		return excludedSortModel;
	}

	protected AsExcludedSortModel createExcludedSortModel2()
	{
		final AsExcludedSortModel excludedSortModel = new AsExcludedSortModel();
		excludedSortModel.setUid(UID_2);
		excludedSortModel.setCode(CODE_2);
		excludedSortModel.setPriority(PRIORITY_2);
		excludedSortModel.setName(NAME_2, LOCALE);

		return excludedSortModel;
	}

	protected AsExcludedSort createExcludedSortData1()
	{
		final AsExcludedSort excludedSortData = new AsExcludedSort();
		excludedSortData.setUid(UID_1);
		excludedSortData.setCode(CODE_1);
		excludedSortData.setPriority(PRIORITY_1);
		excludedSortData.setName(Map.of(LANGUAGE, NAME_1));

		return excludedSortData;
	}

	protected AsExcludedSort createExcludedSortData2()
	{
		final AsExcludedSort excludedSortData = new AsExcludedSort();
		excludedSortData.setUid(UID_2);
		excludedSortData.setCode(CODE_2);
		excludedSortData.setPriority(PRIORITY_2);
		excludedSortData.setName(Map.of(LANGUAGE, NAME_2));

		return excludedSortData;
	}
}
