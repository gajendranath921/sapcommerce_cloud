/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.integrationtests.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class DummyHostnameVerifier implements HostnameVerifier
{
	@Override
	public boolean verify(final String hostname, final SSLSession session)
	{
		return true;
	}
}
