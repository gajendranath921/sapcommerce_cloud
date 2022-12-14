/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.attributes;

import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;



public class DynamicAttributesB2BPermissionResultStatusDisplayByEnum implements
		DynamicAttributeHandler<String, B2BPermissionResultModel>
{
	private EnumerationService enumerationService;

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	@Override
	public String get(final B2BPermissionResultModel permissionResult)
	{
		final String ret = StringUtils.EMPTY;
		if (permissionResult == null)
		{
			throw new IllegalArgumentException("Item model is required");
		}
		if (permissionResult.getStatus() == null)
		{
			return ret;
		}
		return enumerationService.getEnumerationName(permissionResult.getStatus());
	}

	@Override
	public void set(final B2BPermissionResultModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}

}
