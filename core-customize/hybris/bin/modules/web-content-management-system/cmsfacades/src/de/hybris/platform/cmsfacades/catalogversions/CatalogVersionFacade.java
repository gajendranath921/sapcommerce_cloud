/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.catalogversions;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;

import java.util.List;


/**
 * Facade interface which deals with methods related to catalog version operations.
 */
public interface CatalogVersionFacade
{
	/**
	 * Retrieve catalog version information, including page display conditions.
	 *
	 * @param catalogId
	 *           - the catalog identifier
	 * @param versionId
	 *           - the catalog version identifier
	 * @return a {@code CatalogVersionData}
	 * @throws CMSItemNotFoundException
	 *            when the given catalog and/or version is not valid
	 */
	CatalogVersionData getCatalogVersion(String catalogId, String versionId) throws CMSItemNotFoundException;

	/**
	 * Retrieve content catalog versions for a catalog or for all child catalogs.
	 *
	 * @param siteId - the site identifier
	 * @param catalogId - the catalog identifier
	 * @param versionId - the catalog version identifier
	 * @return the list of content catalog versions for given site, catalog or for all child catalogs.
	 */
	List<CatalogVersionData> getWritableContentCatalogVersionTargets(String siteId, String catalogId, String versionId);
}
