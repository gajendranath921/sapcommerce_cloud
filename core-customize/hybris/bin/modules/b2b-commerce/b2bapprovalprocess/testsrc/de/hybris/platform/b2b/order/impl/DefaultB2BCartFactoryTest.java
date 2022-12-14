/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.order.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.CartModel;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test that B2BCustomer Carts get created with parent B2BUnit
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BCartFactoryTest extends B2BIntegrationTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BCartFactoryTest.class);

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}

	@Test
	public void shouldCreateCardAndAssignUnit()
	{
		final String userId = "IC CEO";
		final B2BCustomerModel user = login(userId);
		final B2BUnitModel unit = b2bUnitService.getParent(user);
		// Create automatically include B2BUnit
		CartModel cartModel = b2bCartFactory.createCart();
		// Fetch new instance to assert it was indeed saved
		cartModel = (CartModel) modelService.get(cartModel.getPk());
		// Assert cart parent b2bunit is b2bemployee's b2bunit
		Assert.assertEquals(unit, cartModel.getUnit());
	}
}
