/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;


/**
 * Context attribute
 *
 */
public class ContextAttributeImpl implements ContextAttribute
{
	private String name;
	private String value;

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public void setValue(final String value)
	{
		this.value = value;
	}
}
