/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.interceptor.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link AbstractBundleRuleIDPrepareInterceptor}
 * 
 */
@IntegrationTest
public class ConfigurableBundleSelectionCriteriaIDPrepareInterceptorIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(ConfigurableBundleSelectionCriteriaIDPrepareInterceptorIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BaseSiteService baseSiteService;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for ConfigurableBundleSelectionCriteriaIDPrepareInterceptorIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for ConfigurableBundleSelectionCriteriaIDPrepareInterceptorIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
	}

	@Test
	public void testGenerateBundleSelectionCriteriaID()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();

		final PickExactlyNBundleSelectionCriteriaModel selecCrit2 = modelService
				.create(PickExactlyNBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit2.getId());
		selecCrit2.setCatalogVersion(catalogVersion);
		selecCrit2.setN(1);
		modelService.save(selecCrit2);
		Assert.assertNotNull(selecCrit2.getId());
		LOG.info("selecCrit2.id: " + selecCrit2.getId());

		final PickNToMBundleSelectionCriteriaModel selecCrit3 = modelService.create(PickNToMBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit3.getId());
		selecCrit3.setCatalogVersion(catalogVersion);
		selecCrit3.setN(1);
		selecCrit3.setM(2);
		modelService.save(selecCrit3);
		Assert.assertNotNull(selecCrit3.getId());
		Assert.assertNotEquals(selecCrit2.getId(), selecCrit3.getId());
		LOG.info("selecCrit3.id: " + selecCrit3.getId());

		final PickNToMBundleSelectionCriteriaModel selecCrit5 = modelService.create(PickNToMBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit5.getId());
		selecCrit5.setCatalogVersion(catalogVersion);
		selecCrit5.setN(1);
		selecCrit5.setM(2);
		modelService.save(selecCrit5);
		Assert.assertNotNull(selecCrit5.getId());
		Assert.assertNotEquals(selecCrit2.getId(), selecCrit5.getId());
		Assert.assertNotEquals(selecCrit3.getId(), selecCrit5.getId());
		LOG.info("selecCrit5.id: " + selecCrit5.getId());
	}
}
