/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.hybris.backoffice.solrsearch.cache.impl.BackofficeSolrFacetSearchConfigCache;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.enums.SolrServerModes;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hybris.backoffice.solrsearch.model.BackofficeIndexedTypeToSolrFacetSearchConfigModel;


public class DefaultBackofficeFacetSearchConfigServiceIntegrationTest extends ServicelayerTransactionalTest
{

	public static final String TEST_CONFIG = "testConfig";
	public static final String PRODUCT_INDEXED_TYPE = "ProductIndexedType";
	public static final String CATALOG_INDEXED_TYPE = "CatalogIndexedType";
	public static final String USER_INDEXED_TYPE = "UserIndexedType";
	public static final String PRODUCT = "Product";
	public static final String CATALOG = "Catalog";
	public static final String USER = "User";

	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private BackofficeSolrFacetSearchConfigService backofficeSolrFacetSearchConfigService;
	@Resource
	private BackofficeSolrFacetSearchConfigCache backofficeSolrFacetSearchConfigCache;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testGetFacetSearchConfig() throws Exception
	{
		final SolrFacetSearchConfigModel config = createConfig(TEST_CONFIG);

		final SolrIndexedTypeModel productType = createIndexedType(PRODUCT, PRODUCT_INDEXED_TYPE);
		final SolrIndexedTypeModel catalogType = createIndexedType(CATALOG, CATALOG_INDEXED_TYPE);
		final SolrIndexedTypeModel userType = createIndexedType(USER, USER_INDEXED_TYPE);

		productType.setSolrFacetSearchConfig(config);
		catalogType.setSolrFacetSearchConfig(config);
		userType.setSolrFacetSearchConfig(config);

		config.setSolrIndexedTypes(Lists.newArrayList(productType, catalogType, userType));
		createBackofficeIndexedConfig(PRODUCT, config);
		createBackofficeIndexedConfig(CATALOG, config);
		createBackofficeIndexedConfig(USER, config);
		modelService.saveAll();

		final FacetSearchConfig cfgForProduct = backofficeSolrFacetSearchConfigService.getFacetSearchConfig(PRODUCT);
		final FacetSearchConfig cfgForCatalog = backofficeSolrFacetSearchConfigService.getFacetSearchConfig(CATALOG);
		final FacetSearchConfig cfgForUser = backofficeSolrFacetSearchConfigService.getFacetSearchConfig(USER);
		assertEquals(TEST_CONFIG,cfgForProduct.getName());
		assertEquals(TEST_CONFIG, cfgForCatalog.getName());
		assertEquals(TEST_CONFIG, cfgForUser.getName());
	}

	@Test
	public void testGetAllMappedFacetSearchConfigs()
	{
		final SolrFacetSearchConfigModel config = createConfig(TEST_CONFIG);

		final SolrIndexedTypeModel productType = createIndexedType(PRODUCT, PRODUCT_INDEXED_TYPE);
		final SolrIndexedTypeModel catalogType = createIndexedType(CATALOG, CATALOG_INDEXED_TYPE);
		final SolrIndexedTypeModel userType = createIndexedType(USER, USER_INDEXED_TYPE);

		productType.setSolrFacetSearchConfig(config);
		catalogType.setSolrFacetSearchConfig(config);
		userType.setSolrFacetSearchConfig(config);

		config.setSolrIndexedTypes(Lists.newArrayList(productType, catalogType, userType));
		modelService.saveAll();

		Collection<FacetSearchConfig> allMappedConfigs = backofficeSolrFacetSearchConfigService.getAllMappedFacetSearchConfigs();

		assertNotNull(allMappedConfigs);
		assertEquals(0, allMappedConfigs.size());

		createBackofficeIndexedConfig(PRODUCT, config);
		createBackofficeIndexedConfig(CATALOG, config);
		createBackofficeIndexedConfig(USER, config);
		modelService.saveAll();

		allMappedConfigs = backofficeSolrFacetSearchConfigService.getAllMappedFacetSearchConfigs();

		assertNotNull(allMappedConfigs);
		assertEquals(3, allMappedConfigs.size());
	}

	@Test
	public void testGetSolrIndexedType() throws Exception
	{
		final SolrFacetSearchConfigModel config = createConfig(TEST_CONFIG);

		final SolrIndexedTypeModel catalogType = createIndexedType(CATALOG, CATALOG_INDEXED_TYPE);
		catalogType.setSolrFacetSearchConfig(config);
		config.setSolrIndexedTypes(Lists.newArrayList(catalogType));
		createBackofficeIndexedConfig(CATALOG, config);
		modelService.saveAll();

		final SolrIndexedTypeModel catalogIdxType = backofficeSolrFacetSearchConfigService.getIndexedTypeModel(CATALOG);
		assertNotNull(catalogIdxType);
		assertEquals(CATALOG_INDEXED_TYPE, catalogIdxType.getIdentifier());

		SolrIndexedTypeModel productIdxType = backofficeSolrFacetSearchConfigService.getIndexedTypeModel(PRODUCT);
		assertNull(productIdxType);

		productIdxType = createIndexedType(PRODUCT, PRODUCT_INDEXED_TYPE);
		productIdxType.setSolrFacetSearchConfig(config);
		final ArrayList<SolrIndexedTypeModel> configuredTypes = Lists.newArrayList(config.getSolrIndexedTypes());
		configuredTypes.add(productIdxType);
		config.setSolrIndexedTypes(configuredTypes);
		modelService.saveAll();

		productIdxType = backofficeSolrFacetSearchConfigService.getIndexedTypeModel(PRODUCT);
		assertNull(productIdxType);

		createBackofficeIndexedConfig(PRODUCT, config);
		modelService.saveAll();
		backofficeSolrFacetSearchConfigCache.invalidateCache();

		productIdxType = backofficeSolrFacetSearchConfigService.getIndexedTypeModel(PRODUCT);
		assertNotNull(productIdxType);
		assertEquals(PRODUCT_INDEXED_TYPE, productIdxType.getIdentifier());
	}

	@Test
	public void testIsSolrSearchConfiguredForType() throws Exception
	{
		final SolrFacetSearchConfigModel config = createConfig(TEST_CONFIG);

		final SolrIndexedTypeModel productType = createIndexedType(PRODUCT, PRODUCT_INDEXED_TYPE);
		final SolrIndexedTypeModel catalogType = createIndexedType(CATALOG, CATALOG_INDEXED_TYPE);
		final SolrIndexedTypeModel userType = createIndexedType(USER, USER_INDEXED_TYPE);

		productType.setSolrFacetSearchConfig(config);
		catalogType.setSolrFacetSearchConfig(config);
		userType.setSolrFacetSearchConfig(config);

		config.setSolrIndexedTypes(Lists.newArrayList(productType, catalogType, userType));
		modelService.saveAll();

		// Check for product
		boolean isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(PRODUCT);
		assertFalse(isConfigured);

		BackofficeIndexedTypeToSolrFacetSearchConfigModel backIdxCfg = createBackofficeIndexedConfig(PRODUCT, config);
		modelService.save(backIdxCfg);
		backofficeSolrFacetSearchConfigCache.invalidateCache();
		isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(PRODUCT);
		assertTrue(isConfigured);

		// check for Catalog
		isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(CATALOG);
		assertFalse(isConfigured);

		backIdxCfg = createBackofficeIndexedConfig(CATALOG, config);
		modelService.save(backIdxCfg);
		backofficeSolrFacetSearchConfigCache.invalidateCache();
		isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(CATALOG);
		assertTrue(isConfigured);
	}

	@Test
	public void testConfigForParentType()
	{
		final SolrFacetSearchConfigModel config = createConfig(TEST_CONFIG);

		final SolrIndexedTypeModel productType = createIndexedType(PRODUCT, PRODUCT_INDEXED_TYPE);

		productType.setSolrFacetSearchConfig(config);

		config.setSolrIndexedTypes(Lists.newArrayList(productType));
		modelService.saveAll();

		// Check for product
		boolean isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(ProductModel._TYPECODE);
		assertFalse(isConfigured);

		// Check for variant
		isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(VariantProductModel._TYPECODE);
		assertFalse(isConfigured);

		final BackofficeIndexedTypeToSolrFacetSearchConfigModel backIdxCfg = createBackofficeIndexedConfig(ProductModel._TYPECODE,
				config);
		modelService.save(backIdxCfg);
		backofficeSolrFacetSearchConfigCache.invalidateCache();
		isConfigured = backofficeSolrFacetSearchConfigService.isValidSearchConfiguredForType(ProductModel._TYPECODE);
		assertTrue(isConfigured);
	}

	private SolrFacetSearchConfigModel createConfig(final String name)
	{
		final SolrSearchConfigModel solrConfig = modelService.create(SolrSearchConfigModel.class);
		solrConfig.setPageSize(Integer.valueOf(100));
		solrConfig.setLegacyMode(false);

		final SolrServerConfigModel serverConfigModel = modelService.create(SolrServerConfigModel.class);
		serverConfigModel.setName(name);
		serverConfigModel.setMode(SolrServerModes.STANDALONE);

		final SolrIndexConfigModel indexConfigModel = modelService.create(SolrIndexConfigModel.class);
		indexConfigModel.setName(name);

		final SolrFacetSearchConfigModel cfgModel = modelService.create(SolrFacetSearchConfigModel.class);
		cfgModel.setName(name);
		cfgModel.setEnabledLanguageFallbackMechanism(true);
		cfgModel.setSolrSearchConfig(solrConfig);
		cfgModel.setSolrServerConfig(serverConfigModel);
		cfgModel.setSolrIndexConfig(indexConfigModel);

		return cfgModel;
	}

	private SolrIndexedTypeModel createIndexedType(final String typeCode, final String name)
	{
		final ComposedTypeModel composedTypeForCode = typeService.getComposedTypeForCode(typeCode);
		final SolrIndexedTypeModel indexedType = modelService.create(SolrIndexedTypeModel.class);
		indexedType.setIdentifier(name);
		indexedType.setType(composedTypeForCode);
		return indexedType;
	}

	private BackofficeIndexedTypeToSolrFacetSearchConfigModel createBackofficeIndexedConfig(final String typeCode,
			final SolrFacetSearchConfigModel cfgModel)
	{
		final ComposedTypeModel composedTypeForCode = typeService.getComposedTypeForCode(typeCode);
		final BackofficeIndexedTypeToSolrFacetSearchConfigModel model = modelService
				.create(BackofficeIndexedTypeToSolrFacetSearchConfigModel.class);
		model.setIndexedType(composedTypeForCode);
		model.setSolrFacetSearchConfig(cfgModel);
		return model;
	}

}
