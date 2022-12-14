/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.rendering.impl;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.rendering.RestrictionContextProvider;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link RestrictionContextProvider}. Sets a restriction in session to be available elsewhere.
 */
public class DefaultRestrictionContextProvider implements RestrictionContextProvider
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private SessionService sessionService;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public RestrictionData getRestrictionInContext()
	{
		Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM);
		if( value != null )
		{
			return (RestrictionData) value;
		}

		return null;
	}

	@Override
	public void setRestrictionInContext(RestrictionData restrictionData)
	{
		getSessionService().setAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM, restrictionData);
	}

	@Override
	public void removeRestrictionFromContext()
	{
		getSessionService().removeAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM);
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
