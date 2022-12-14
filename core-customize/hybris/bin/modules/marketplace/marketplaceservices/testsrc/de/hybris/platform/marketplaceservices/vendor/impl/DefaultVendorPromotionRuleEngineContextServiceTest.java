/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.vendor.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultVendorPromotionRuleEngineContextServiceTest
{
	private final String contextName = "default";

	private DefaultVendorPromotionRuleEngineContextService vendorPromotionRuleEngineContextService;

	@Mock
	private AbstractRuleEngineContextModel context;

	@Mock
	private RuleEngineContextDao ruleEngineContextDao;

	@Before
	public void prepare()
	{
		vendorPromotionRuleEngineContextService = new DefaultVendorPromotionRuleEngineContextService();
		vendorPromotionRuleEngineContextService.setRuleEngineContextDao(ruleEngineContextDao);

	}

	@Test
	public void testfindVendorRuleEngineContextByName()
	{
		Mockito.doReturn(context).when(ruleEngineContextDao).findRuleEngineContextByName(any());
		final AbstractRuleEngineContextModel returnContext = vendorPromotionRuleEngineContextService
				.findVendorRuleEngineContextByName(contextName);
		assertEquals(context, returnContext);
	}

}
