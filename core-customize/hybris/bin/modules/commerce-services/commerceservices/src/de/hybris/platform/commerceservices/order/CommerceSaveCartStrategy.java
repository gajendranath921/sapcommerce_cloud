/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;


/**
 * A strategy interface for saving a cart.
 */
public interface CommerceSaveCartStrategy
{
	/**
	 * Method for explicitly saving a cart along with additional parameters
	 *
	 * @param parameters
	 *           {@link CommerceSaveCartParameter} parameter object that holds the cart to be saved along with some
	 *           additional details such as a name and a description for this cart
	 * @return {@link CommerceSaveCartResult}
	 * @throws CommerceSaveCartException
	 *            if cart cannot be saved
	 */
	CommerceSaveCartResult saveCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException;
}
