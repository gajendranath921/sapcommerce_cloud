/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.promotion.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.promotion.data.PromotionRestrictionData;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test suite for {@link PromotionRestrictionPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionRestrictionPopulatorTest
{
	private static final String RESTRICTION_TYPE = "restrictionType";
	private static final String DESCRIPTION = "desc";
	private PromotionRestrictionPopulator promotionRestrictionPopulator;
	@Mock
	private AbstractPromotionRestrictionModel promotionModel;

	@Before
	public void setUp()
	{
		promotionRestrictionPopulator = new PromotionRestrictionPopulator();
		given(promotionModel.getRestrictionType()).willReturn(RESTRICTION_TYPE);
		given(promotionModel.getRenderedDescription()).willReturn(DESCRIPTION);
	}

	@Test
	public void testPopulate()
	{
		final PromotionRestrictionData promotionData = new PromotionRestrictionData();
		promotionRestrictionPopulator.populate(promotionModel, promotionData);

		Assert.assertEquals(RESTRICTION_TYPE, promotionData.getRestrictionType());
		Assert.assertEquals(DESCRIPTION, promotionData.getDescription());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullSource()
	{
		final PromotionRestrictionData promotionData = new PromotionRestrictionData();
		promotionRestrictionPopulator.populate(null, promotionData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullTarget()
	{
		promotionRestrictionPopulator.populate(promotionModel, null);
	}

}
