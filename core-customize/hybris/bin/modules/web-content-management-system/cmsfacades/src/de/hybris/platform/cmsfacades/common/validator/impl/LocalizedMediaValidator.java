/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.validator.impl;


import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.validator.LocalizedTypeValidator;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;


/**
 * Default validator to use for validating localized attributes of type media. This implementation uses the
 * {@link MediaService} and the {@link CMSAdminSiteService} to verify whether a given media is valid or not.
 */
public class LocalizedMediaValidator implements LocalizedTypeValidator
{
	private MediaService mediaService;
	private CMSAdminSiteService cmsAdminSiteService;

	@Override
	public void validate(final String language, final String fieldName, final String mediaCode, final Errors errors)
	{
		try
		{
			if (!Objects.isNull(mediaCode))
			{
				final MediaModel media = getMediaService().getMedia(getCmsAdminSiteService().getActiveCatalogVersion(), mediaCode);

				if (Objects.isNull(media))
				{
					reject(language, fieldName, CmsfacadesConstants.INVALID_MEDIA_CODE_L10N, errors);
				}
			}
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			reject(language, fieldName, CmsfacadesConstants.INVALID_MEDIA_CODE_L10N, errors);
		}
	}

	@Override
	public void reject(final String language, final String fieldName, final String errorCode, final Errors errors)
	{
		errors.rejectValue(fieldName, errorCode, new Object[]
				{ language }, null);
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
