/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.storelocator;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.map.Map;


/**
 * Store locator facade. Contains methods related to store locator functionality - finding points of services, sorting
 * them by distance, retrieving details.
 */
public interface StoreLocatorFacade
{
	/**
	 * Gets the locations for query. Returns locations for the given query string that can be postal code or town name.
	 * Locations number is limited to the given value.
	 *
	 * @param searchTerm
	 *           the search term to look locations for. Can be postal code or town name
	 * @param limit
	 *           the maximum number of returned results
	 * @return the map of locations
	 * @throws MapServiceException
	 *            the map service exception
	 */
	Map getLocationsForQuery(String searchTerm, int limit) throws MapServiceException;

	/**
	 * Gets the point of service for name that is unique.
	 *
	 * @param posName
	 *           the pos name
	 * @return the found point of service
	 *
	 * @throws LocationServiceException
	 *            if {@link Location} for name can not be found
	 */
	PointOfServiceData getPOSForName(String posName) throws LocationServiceException;


	/**
	 * Gets the point of service for the given location. If location is distance aware also fills information about
	 * distance.
	 *
	 * @param location
	 *           the location
	 * @return the point of service for location
	 */
	PointOfServiceData getPOSForLocation(Location location);
}
