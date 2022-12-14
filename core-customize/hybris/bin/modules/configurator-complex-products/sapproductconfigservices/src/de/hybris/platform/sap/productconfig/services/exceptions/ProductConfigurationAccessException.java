/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.exceptions;

/**
 * Thrown if an action on a product configuration runtime instance is forbidden
 */
public class ProductConfigurationAccessException extends IllegalStateException
{
	/**
	 * Constructor
	 * 
	 * @param message
	 *           Message text
	 */
	public ProductConfigurationAccessException(final String message)
	{
		super(message);
	}
}
