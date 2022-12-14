/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSAdminPageServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private CMSAdminPageService cmsAdminPageService;
	@Resource
	private TypeService typeService;
	@Resource
	private CatalogVersionService catalogVersionService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/cmsMultiCountryTestData.csv", "UTF-8");
		importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");
	}

	@Test
	public void shouldFindAllContentPageTemplates()
	{
		catalogVersionService.setSessionCatalogVersion("MultiCountryTestContentCatalog", "StagedVersion");

		final Collection<PageTemplateModel> results = cmsAdminPageService.getAllRestrictedPageTemplates(true,
				(CMSPageTypeModel) typeService.getComposedTypeForClass(ContentPageModel.class));

		assertThat(results, hasItems(hasProperty("uid", equalTo("SearchResultsListPageTemplate")),
				hasProperty("uid", equalTo("SearchResultsGridPageTemplate")),
				hasProperty("uid", equalTo("SearchResultsEmptyPageTemplate")), hasProperty("uid", equalTo("HomePageTemplate"))));
	}

}
