/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.OrderPromotion;
import de.hybris.platform.promotions.jalo.ProductPromotion;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionsPopulatorTest
{
	private static final String PROMOTION_CODE = "proCode";
	private static final String PROMOTION_TYPE = "proType";


	@Mock(lenient = true)
	private CartService cartService;
	@Mock
	private PromotionResultService promotionResultService;
	@Mock(lenient = true)
	private PromotionsService promotionService;

	private final PromotionsPopulator promotionsPopulator = new PromotionsPopulator();
	private AbstractPopulatingConverter<AbstractPromotionModel, PromotionData> promotionsConverter;

	@Before
	public void setUp()
	{
		promotionsPopulator.setCartService(cartService);
		promotionsPopulator.setPromotionResultService(promotionResultService);
		promotionsPopulator.setPromotionService(promotionService);

		promotionsConverter = new ConverterFactory<AbstractPromotionModel, PromotionData, PromotionsPopulator>().create(
				PromotionData.class, promotionsPopulator);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		promotionsConverter.convert(null, mock(PromotionData.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenPrototypeIsNull()
	{
		promotionsConverter.convert(mock(AbstractPromotionModel.class), null);
	}


	@Test
	public void testConvert()
	{
		final ProductPromotionModel source = mock(ProductPromotionModel.class);
		final CartModel cartModel = mock(CartModel.class);
		final ProductPromotion abstractPromotion = mock(ProductPromotion.class);
		final PromotionResult promotionResult = mock(PromotionResult.class, withSettings().lenient());
		final SessionContext ctx = mock(SessionContext.class);
		final PromotionOrderResults promoOrderResults = new PromotionOrderResults(ctx, mock(Cart.class),
				Collections.singletonList(promotionResult), 0D);
		promoOrderResults.invalidateCache();

		// Setting descriptions cannot be tested because PromotionResult.getDescription() is final method
		given(promotionResult.getPromotion()).willReturn(abstractPromotion);
		given(Boolean.valueOf(promotionResult.getFired(ctx))).willReturn(Boolean.FALSE);
		given(promotionService.getPromotionResults(cartModel)).willReturn(promoOrderResults);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(source.getCode()).willReturn(PROMOTION_CODE);
		given(source.getPromotionType()).willReturn(PROMOTION_TYPE);

		final PromotionData result = promotionsConverter.convert(source);

		Assert.assertEquals(PROMOTION_CODE, result.getCode());
		Assert.assertEquals(PROMOTION_TYPE, result.getPromotionType());
	}


	@Test
	public void testConvertOrderPromotion()
	{
		final ProductPromotionModel source = mock(ProductPromotionModel.class);
		final CartModel cartModel = mock(CartModel.class);
		final OrderPromotion abstractPromotion = mock(OrderPromotion.class);
		final PromotionResult promotionResult = mock(PromotionResult.class, withSettings().lenient());
		final PromotionOrderResults promoOrderResults = new PromotionOrderResults(mock(SessionContext.class), mock(Cart.class),
				Collections.singletonList(promotionResult), 0D);

		given(promotionResult.getPromotion()).willReturn(abstractPromotion);
		given(promotionService.getPromotionResults(cartModel)).willReturn(promoOrderResults);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(source.getCode()).willReturn(PROMOTION_CODE);
		given(source.getPromotionType()).willReturn(PROMOTION_TYPE);

		final PromotionData result = promotionsConverter.convert(source);

		Assert.assertEquals(PROMOTION_CODE, result.getCode());
		Assert.assertEquals(PROMOTION_TYPE, result.getPromotionType());
	}

}
