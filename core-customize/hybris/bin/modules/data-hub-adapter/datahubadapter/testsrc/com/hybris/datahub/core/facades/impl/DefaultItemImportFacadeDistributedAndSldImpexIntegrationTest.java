/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.facades.impl;

import org.junit.Before;

public class DefaultItemImportFacadeDistributedAndSldImpexIntegrationTest extends AbstractItemImportFacadeIntegrationTest
{
	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		enableDistributedImpexAndSld(true, true);
	}
}
