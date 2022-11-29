/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationSavedCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfiguratorCCPSavedCartIntegrationControllerTest
{
	private static final String CART_ID = "001";

	private static final String CONFIG_ID = "config123";

	@Mock
	private ConfigurationSavedCartIntegrationFacade configSavedCartFacade;

	@Mock
	private ConfigurationOverviewFacade configOvFacade;

	@Mock
	private DataMapper dataMapper;

	@InjectMocks
	private ProductConfiguratorCCPSavedCartIntegrationController classUnderTest;

	private final ConfigurationOverviewData configOvData = new ConfigurationOverviewData();
	private final ConfigurationOverviewWsDTO configOvWsDTO = new ConfigurationOverviewWsDTO();

	@Before
	public void setUp() throws CommerceSaveCartException
	{
		configOvData.setId(CONFIG_ID);
		configOvWsDTO.setId(CONFIG_ID);
		given(configSavedCartFacade.getConfiguration(CART_ID, 3)).willThrow(new CommerceSaveCartException("TEST"));
		given(configSavedCartFacade.getConfiguration(CART_ID, 0)).willReturn(configOvData);
		given(configOvFacade.getOverviewForConfiguration(CONFIG_ID, configOvData)).willReturn(configOvData);
		given(dataMapper.map(configOvData, ConfigurationOverviewWsDTO.class)).willReturn(configOvWsDTO);
	}

	@Test
	public void testReadConfigurationOveviewReturnsConfigId() throws CommerceSaveCartException
	{
		final ConfigurationOverviewData result = classUnderTest.readConfigurationOverview(CART_ID, 0);
		assertNotNull(result);
		assertEquals(CONFIG_ID, result.getId());
	}

	@Test
	public void testReadConfigurationOveviewMapsData() throws CommerceSaveCartException
	{
		classUnderTest.readConfigurationOverview(CART_ID, 0);
		verify(configOvFacade).getOverviewForConfiguration(CONFIG_ID, configOvData);
	}

	@Test
	public void testGetConfigurationOverviewForSavedCart() throws CommerceSaveCartException
	{
		final ConfigurationOverviewWsDTO resultWsDTO = classUnderTest.getConfigurationOverviewForSavedCart(CART_ID, 0);
		assertNotNull(resultWsDTO);
		assertEquals(CONFIG_ID, resultWsDTO.getId());
	}

	@Test(expected = NotFoundException.class)
	public void testGetConfigurationOverviewForSavedCartNotFound() throws CommerceSaveCartException
	{
		classUnderTest.getConfigurationOverviewForSavedCart(CART_ID, 3);
	}


}
