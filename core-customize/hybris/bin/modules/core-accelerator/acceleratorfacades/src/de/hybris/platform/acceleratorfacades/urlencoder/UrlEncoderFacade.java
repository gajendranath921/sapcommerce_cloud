/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.urlencoder;

import de.hybris.platform.acceleratorfacades.urlencoder.data.UrlEncoderData;

import java.util.List;


/**
 *
 * This facade will be used by UrlEncoderFilter to process the URL for UrlEncoding.
 *
 */
public interface UrlEncoderFacade
{
	/**
	 * Checks if the given urlEncodingAttribute holds a valid value for injection into the URL.
	 *
	 * @param attributeName
	 *           encodoing attribute name to check
	 * @param value
	 *           encoding attribute value to check
	 * @return boolean
	 */
	boolean isValid(String attributeName, String value);


	/**
	 * Gets the list of attributes and it's values that has to be encoded in the url. The list of attributes for each
	 * site will be configured in {@link de.hybris.platform.cms2.model.site.CMSSiteModel} and the data objects that holds
	 * the attributes and it's values will be set in SessionService attributes.
	 *
	 * @return List of {@link UrlEncoderData} objects that holds the encoding attribute's values.
	 */
	List<UrlEncoderData> getCurrentUrlEncodingData();

	/**
	 * Updates the Store/Site based on the change in value of the urlEncodingAttributes. Only specific encoding
	 * attributes are allowed to change the site.
	 */
	void updateSiteFromUrlEncodingData();

	/**
	 * Gets the urlEncoding pattern that has to be injected into the URL. This pattern is calculated based on the input
	 * URL and encoding attributes configured for specific site. This method will also update the url encoding values
	 * stored in the session.
	 *
	 *
	 * @param url
	 *           input URL to build pattern
	 * @param contextPath
	 *           context with encoding attributes
	 * @return the pattern
	 */
	String calculateAndUpdateUrlEncodingData(String url, String contextPath);
}
