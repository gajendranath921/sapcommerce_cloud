/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates an {@link AbstractPageData} dto from a {@link AbstractPageModel} page
 */
public class AbstractPageDataPopulator implements Populator<AbstractPageModel, AbstractPageData>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public void populate(final AbstractPageModel source, final AbstractPageData target) throws ConversionException
	{
		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setCatalogVersionUuid(getUniqueItemIdentifierService().getItemData(source.getCatalogVersion())
				.orElseThrow(
						() -> new UnknownIdentifierException("Cannot generate uuid for catalogVersion in AbstractPageDataPopulator"))
				.getItemId());
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
}
