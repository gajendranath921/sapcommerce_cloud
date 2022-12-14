/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.cart.action.impl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartEntryModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RemoveCartEntryActionHandlerTest
{
	@InjectMocks
	private final RemoveCartEntryActionHandler removeCartEntryActionHandler = new RemoveCartEntryActionHandler();

	@Mock
	private CartFacade cartFacade;

	@Test
	public void shouldProvideExcpetedErrorMessageKey()
	{
		Assert.assertEquals("Error message key should be as expected.", "basket.page.error.remove",
				removeCartEntryActionHandler.getErrorMessageKey());
	}

	@Test
	public void shouldProvideExcpetedSuccessMessageKey()
	{
		Assert.assertEquals("Error message key should be as expected.", "basket.page.message.remove",
				removeCartEntryActionHandler.getSuccessMessageKey());
	}

	@Test
	public void shouldUpdateCartAndRedirectOnSuccessfulExecute() throws Exception
	{
		final Optional<String> redirecUrl = removeCartEntryActionHandler
				.handleAction(Arrays.asList(Long.valueOf(2), Long.valueOf(3)));
		verify(cartFacade).updateCartEntry(2, 0);
		verify(cartFacade).updateCartEntry(3, 0);
		Assert.assertFalse("Redirect Url not expected. This action uses the default redirect.", redirecUrl.isPresent());
	}

	@Test(expected = CartEntryActionException.class)
	public void shouldTranslateCommerceCartModificationException()
			throws CommerceCartModificationException, CartEntryActionException
	{
		given(cartFacade.updateCartEntry(anyLong(), anyLong()))
				.willThrow(new CommerceCartModificationException("Exception thrown by mock call"));
		removeCartEntryActionHandler.handleAction(Collections.singletonList(Long.valueOf(3)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotHandleActionIfEntryNumbersAreNull() throws CommerceCartModificationException, CartEntryActionException
	{
		removeCartEntryActionHandler.handleAction(null);
	}

	@Test
	public void shouldSupportActionForAnyCartEntry()
	{
		Assert.assertTrue("Should support remove operation for any cart entry, even null",
				removeCartEntryActionHandler.supports(null));
		Assert.assertTrue("Should support remove operation for any cart entry",
				removeCartEntryActionHandler.supports(mock(CartEntryModel.class)));
	}
}
