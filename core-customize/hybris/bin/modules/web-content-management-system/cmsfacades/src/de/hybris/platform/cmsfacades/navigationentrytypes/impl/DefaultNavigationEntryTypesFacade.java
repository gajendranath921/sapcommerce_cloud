/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.navigationentrytypes.impl;

import de.hybris.platform.cmsfacades.data.NavigationEntryTypeData;
import de.hybris.platform.cmsfacades.navigationentrytypes.NavigationEntryTypesFacade;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NavigationEntryTypesFacade}
 * @deprecated since 1811 - no longer needed
 */
@Deprecated(since = "1811", forRemoval = true)
public class DefaultNavigationEntryTypesFacade implements NavigationEntryTypesFacade
{
	private NavigationEntryConverterRegistry navigationEntryConverterRegistry;

	@Override
	public List<NavigationEntryTypeData> getNavigationEntryTypes()
	{
		return getNavigationEntryConverterRegistry().getSupportedItemTypes().map(itemTypes -> itemTypes.stream().map(itemType -> {
			final NavigationEntryTypeData navigationEntryTypeData = new NavigationEntryTypeData();
			navigationEntryTypeData.setItemType(itemType);
			return navigationEntryTypeData;
		}).collect(Collectors.toList())).orElse(Collections.emptyList());
	}

	protected NavigationEntryConverterRegistry getNavigationEntryConverterRegistry()
	{
		return navigationEntryConverterRegistry;
	}

	@Required
	public void setNavigationEntryConverterRegistry(final NavigationEntryConverterRegistry navigationEntryConverterRegistry)
	{
		this.navigationEntryConverterRegistry = navigationEntryConverterRegistry;
	}
}
