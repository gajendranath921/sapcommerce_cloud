/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.urlresolver.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the UrlResolutionService.
 */
public class DefaultSiteBaseUrlResolutionService implements SiteBaseUrlResolutionService
{
	private static final String CMS_SITE_MODEL_CANNOT_BE_NULL_MSG = "CMS site model cannot be null";
	private ConfigurationService configurationService;
	private Map<SiteChannel, String> contextRoots;
	private UrlEncoderService urlEncoderService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Map<SiteChannel, String> getContextRoots()
	{
		return contextRoots;
	}

	@Required
	public void setContextRoots(final Map<SiteChannel, String> contextRoots)
	{
		this.contextRoots = contextRoots;
	}

	protected UrlEncoderService getUrlEncoderService()
	{
		return urlEncoderService;
	}

	@Required
	public void setUrlEncoderService(final UrlEncoderService urlEncoderService)
	{
		this.urlEncoderService = urlEncoderService;
	}


	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final String encodingAttributes, final boolean secure,
			final String path)
	{
		validateParameterNotNull(site, CMS_SITE_MODEL_CANNOT_BE_NULL_MSG);

		final String url = cleanupUrl(lookupConfig("website." + site.getUid() + (secure ? ".https" : ".http")));
		final String websiteUrl = getWebsiteUrl(url, encodingAttributes, path);
		return StringUtils.isNotBlank(websiteUrl) ? websiteUrl : getDefaultWebsiteUrlForSite(site, secure, path);
	}


	protected String getWebsiteUrl(String baseUrl, final String encodingAttributes, final String path)
	{
		//		 if url contains ? remove everything after ? then add path then add back the query string
		//		 this is so website urls in config files can have query strings and urls in emails will be
		//		 formatted correctly
		if (StringUtils.isBlank(baseUrl)) {
			return null;
		}
		if (baseUrl.contains("?"))
		{
			final String queryString = baseUrl.substring(baseUrl.indexOf('?'));
			final String tmpUrl = baseUrl.substring(0, baseUrl.indexOf('?'));
			return cleanupUrl(tmpUrl) + (StringUtils.isNotBlank(encodingAttributes) ? encodingAttributes : "")
					+ (path == null ? "" : path) + "/" + queryString;
		}
		return baseUrl + (StringUtils.isNotBlank(encodingAttributes) ? encodingAttributes : "") + (path == null ? "" : path);
	}

	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final boolean secure, final String path)
	{
		return getWebsiteUrlForSite(site, getUrlEncoderService().getUrlEncodingPattern(), secure, path);
	}


	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final boolean secure, final String path, final String queryParams)
	{
		final String url = getWebsiteUrlForSite(site, secure, path);
		return appendQueryParams(url, queryParams);
	}

	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final String encodingAtrributes, final boolean secure,
			final String path, final String queryParams)
	{
		final String url = getWebsiteUrlForSite(site, encodingAtrributes, secure, path);
		return appendQueryParams(url, queryParams);
	}

	@Override
	public String getMediaUrlForSite(final BaseSiteModel site, final boolean secure, final String path)
	{
		validateParameterNotNull(site, CMS_SITE_MODEL_CANNOT_BE_NULL_MSG);

		final String url = getMediaUrlForSite(site, secure);
		if (url != null)
		{
			return url + (path == null ? "" : path);
		}
		return getDefaultMediaUrlForSite(site, secure) + (path == null ? "" : path);
	}

	@Override
	public String getMediaUrlForSite(final BaseSiteModel site, final boolean secure)
	{
		validateParameterNotNull(site, CMS_SITE_MODEL_CANNOT_BE_NULL_MSG);

		final String url = cleanupUrl(lookupConfig("media." + site.getUid() + (secure ? ".https" : ".http")));
		if (url != null)
		{
			return url;
		}
		return getDefaultMediaUrlForSite(site, secure);
	}


	protected String lookupConfig(final String key)
	{
		return getConfigurationService().getConfiguration().getString(key, null);
	}

	protected String cleanupUrl(final String url)
	{
		if (url != null && url.endsWith("/"))
		{
			return url.substring(0, url.length() - 1);
		}
		return url;
	}

	protected String getDefaultWebsiteUrlForSite(final BaseSiteModel site, final boolean secure, final String path)
	{
		final String contextRoot = getDefaultWebsiteContextRootForSite(site);
		if (contextRoot != null)
		{
			final String schemeHostAndPort;
			if (secure)
			{
				schemeHostAndPort = "https://localhost:" + lookupConfig("tomcat.ssl.port");
			}
			else
			{
				schemeHostAndPort = "http://localhost:" + lookupConfig("tomcat.http.port");
			}

			final String url = schemeHostAndPort + contextRoot + path;
			final String queryParams = "clear=true&site=" + site.getUid();
			return appendQueryParams(url, queryParams);
		}
		return null;
	}

	protected String getDefaultWebsiteContextRootForSite(final BaseSiteModel site)
	{
		final Map<SiteChannel, String> roots = getContextRoots();
		if (site.getChannel() != null && roots.containsKey(site.getChannel()))
		{
			return cleanupUrl(roots.get(site.getChannel()));
		}
		return null;
	}

	protected String appendQueryParams(final String url, final String params)
	{
		if (url.contains("?"))
		{
			return url + "&" + params;
		}
		else
		{
			return url + "?" + params;
		}
	}

	protected String getDefaultMediaUrlForSite(final BaseSiteModel site, final boolean secure)
	{
		if (secure)
		{
			return "https://localhost:" + lookupConfig("tomcat.ssl.port");
		}
		else
		{
			return "http://localhost:" + lookupConfig("tomcat.http.port");
		}
	}
}
