/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteUserTypeIdentificationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteUserTypeIdentificationStrategyTest
{
	private DefaultQuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	@Mock
	private UserService userService;
	@Mock(lenient = true)
	private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
	private UserGroupModel userGroup;
	private String buyerGroup;
	private String sellerGroup;
	private String sellerApproverGroup;
	private UserModel userModel;


	@Before
	public void setUp()
	{
		buyerGroup = "buyerGroup";
		sellerGroup = "sellerGroup";
		sellerApproverGroup = "sellerApproverGroup";
		quoteUserTypeIdentificationStrategy = new DefaultQuoteUserTypeIdentificationStrategy();
		quoteUserTypeIdentificationStrategy.setUserService(userService);
		quoteUserTypeIdentificationStrategy.setBuyerGroup(buyerGroup);
		quoteUserTypeIdentificationStrategy.setSellerGroup(sellerGroup);
		quoteUserTypeIdentificationStrategy.setSellerApproverGroup(sellerApproverGroup);
		userGroup = new UserGroupModel();
		userModel = new UserModel();
		given(quoteUserIdentificationStrategy.getCurrentQuoteUser()).willReturn(userModel);
		given(Boolean.valueOf(userService.isMemberOfGroup(userModel, userGroup))).willReturn(Boolean.TRUE);
	}

	@Test
	public void shouldGetCurrentQuoteUserTypeAsBuyer()
	{
		given(userService.getUserGroupForUID(buyerGroup)).willReturn(userGroup);
		assertEquals("Quote states are wrong", QuoteUserType.BUYER,
				quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel).get());
	}

	@Test
	public void shouldGetCurrentQuoteUserTypeAsSeller()
	{
		given(userService.getUserGroupForUID(sellerGroup)).willReturn(userGroup);
		assertEquals("Quote states are wrong", QuoteUserType.SELLER,
				quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel).get());
	}

	@Test
	public void shouldGetCurrentQuoteUserTypeAsSellerApprover()
	{
		given(userService.getUserGroupForUID(sellerApproverGroup)).willReturn(userGroup);
		assertEquals("Quote states are wrong", QuoteUserType.SELLERAPPROVER,
				quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel).get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetCurrentQuoteUserTypeIfUserIsNull()
	{
		quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(null);
	}

}
