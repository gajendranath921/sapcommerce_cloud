/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class DummyConfigurationsTest
{

	@Test
	public void testDragonCar()
	{
		assertNotNull(new DummyConfigurationWecDragonCarImpl());
	}

	@Test
	public void testKD990Sol()
	{
		assertNotNull(new DummyConfigurationKD990SolImpl());
	}
}
