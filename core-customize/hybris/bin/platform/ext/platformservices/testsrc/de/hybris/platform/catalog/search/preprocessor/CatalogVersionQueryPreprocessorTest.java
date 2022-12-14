/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.catalog.search.preprocessor;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.preprocessor.QueryPreprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CatalogVersionQueryPreprocessorTest
{
	@InjectMocks
	private final QueryPreprocessor preprocessor = new CatalogVersionQueryPreprocessor();
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private FlexibleSearchQuery query;
	@Mock
	private CatalogVersionModel catalogVersion;

	@Test
	public void shouldNotProcessWhenCatalogVersionsInQueryObjectIsNull()
	{
		// given
		given(query.getCatalogVersions()).willReturn(null);

		// when
		preprocessor.process(query);

		// then
		verify(catalogVersionService, times(0)).setSessionCatalogVersions((Collection) anyObject());
	}

	@Test
	public void shouldNotProcessWhenCatalogVersionsInQueryObjectIsEmpty()
	{
		// given
		given(query.getCatalogVersions()).willReturn(Collections.EMPTY_LIST);

		// when
		preprocessor.process(query);

		// then
		verify(catalogVersionService, times(0)).setSessionCatalogVersions(Collections.EMPTY_LIST);
	}

	@Test
	public void shouldProcessWhenThereIsCollectionOfCatalogVersionsInQueryObject()
	{
		// given
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<CatalogVersionModel>();
		catalogVersions.add(catalogVersion);
		given(query.getCatalogVersions()).willReturn(catalogVersions);

		// when
		preprocessor.process(query);

		// then
		verify(catalogVersionService, times(1)).setSessionCatalogVersions(catalogVersions);
	}

}
