/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.catalog.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CatalogHierarchyPopulator;
import de.hybris.platform.commercefacades.catalog.converters.populator.CatalogVersionPopulator;
import de.hybris.platform.commercefacades.catalog.data.CatalogData;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Sets;

import static org.fest.assertions.Assertions.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogHierarchyPopulatorTest
{
	private final CatalogHierarchyPopulator catalogHierarchyPopulator = new CatalogHierarchyPopulator();

	@Mock
	private CatalogVersionPopulator mockCatalogVersionPopulator;

	@Before
	public void setUp()
	{
		catalogHierarchyPopulator.setCatalogVersionPopulator(mockCatalogVersionPopulator);
	}

	@Test
	public void test()
	{
		final Date lastModifiedTime = new Date();
		final Collection<CatalogOption> options = Sets.newHashSet(CatalogOption.BASIC);
		final CatalogData catalogData = new CatalogData();
		catalogData.setId("hwcatalog");
		final CatalogModel catalogModel = Mockito.mock(CatalogModel.class);
		BDDMockito.when(catalogModel.getId()).thenReturn("hwcatalog");
		BDDMockito.when(catalogModel.getName()).thenReturn("Hardware catalog");
		BDDMockito.when(catalogModel.getModifiedtime()).thenReturn(lastModifiedTime);

		final CatalogVersionModel mockCatalogVersion = Mockito.mock(CatalogVersionModel.class);
		BDDMockito.when(mockCatalogVersion.getVersion()).thenReturn("Online");
		BDDMockito.when(catalogModel.getCatalogVersions()).thenReturn(Sets.newHashSet(mockCatalogVersion));


		catalogHierarchyPopulator.populate(catalogModel, catalogData, options);

		assertThat(catalogData.getId()).isEqualTo("hwcatalog");
		assertThat(catalogData.getName()).isEqualTo("Hardware catalog");
		assertThat(catalogData.getLastModified()).isEqualTo(lastModifiedTime);
		assertThat(catalogData.getCatalogVersions()).hasSize(1);
		assertThat(catalogData.getCatalogVersions()).onProperty("url").containsOnly("/hwcatalog/Online");


	}
}
