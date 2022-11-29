/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;

/**
 *   A strategy for delivery mode validation
 *
 */
public interface CommerceDeliveryModeValidationStrategy
{
	void validateDeliveryMode(CommerceCheckoutParameter parameter);
}
