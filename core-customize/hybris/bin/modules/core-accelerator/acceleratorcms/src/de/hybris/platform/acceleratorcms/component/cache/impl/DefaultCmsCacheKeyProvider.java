/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.component.cache.impl;

import de.hybris.platform.acceleratorcms.component.cache.CmsCacheKeyProvider;
import de.hybris.platform.acceleratorcms.model.restrictions.CMSUiExperienceRestrictionModel;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.exceptions.RestrictionEvaluationException;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCatalogRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSInverseRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.regioncache.key.CacheKey;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;


public class DefaultCmsCacheKeyProvider implements CmsCacheKeyProvider<SimpleCMSComponentModel>
{
	private CMSRestrictionService cmsRestrictionService;
	private CMSPageContextService cmsPageContextService;
	private CommerceCommonI18NService commerceCommonI18NService;

	protected CMSRestrictionService getCmsRestrictionService(final HttpServletRequest request)
	{
		if (cmsRestrictionService == null)
		{
			cmsRestrictionService = SpringHelper.getSpringBean(request, "cmsRestrictionService", CMSRestrictionService.class, true);
		}
		return cmsRestrictionService;
	}

	protected CMSPageContextService getCmsPageContextService(final HttpServletRequest request)
	{
		if (cmsPageContextService == null)
		{
			cmsPageContextService = SpringHelper.getSpringBean(request, "cmsPageContextService", CMSPageContextService.class, true);
		}
		return cmsPageContextService;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService(final HttpServletRequest request)
	{
		if (commerceCommonI18NService == null)
		{
			commerceCommonI18NService = SpringHelper.getSpringBean(request, "commerceCommonI18NService",
					CommerceCommonI18NService.class, true);
		}
		return commerceCommonI18NService;
	}

	@Override
	public CacheKey getKey(final HttpServletRequest request, final SimpleCMSComponentModel component)
	{
		return new CmsCacheKey(getKeyInternal(request, component).toString(), Registry.getCurrentTenant().getTenantID());
	}

	protected StringBuilder getKeyInternal(final HttpServletRequest request, final SimpleCMSComponentModel component)
	{
		final StringBuilder key = new StringBuilder();
		key.append(component.getPk().getLongValueAsString());
		key.append(component.getModifiedtime());
		final CurrencyModel currentCurrency = getCommerceCommonI18NService(request).getCurrentCurrency();
		key.append(currentCurrency.getIsocode());
		final LanguageModel currentLanguage = getCommerceCommonI18NService(request).getCurrentLanguage();
		key.append(currentLanguage.getIsocode());

		final List<AbstractRestrictionModel> restrictions = component.getRestrictions();
		if (CollectionUtils.isNotEmpty(restrictions))
		{
			appendRestrictionKeys(request, component, key);
		}

		return key;
	}

	protected void appendRestrictionKeys(final HttpServletRequest request, final SimpleCMSComponentModel component,
			final StringBuilder key)
	{
		for (final AbstractRestrictionModel restriction : component.getRestrictions())
		{
			try
			{
				if (getCmsRestrictionService(request).evaluate(restriction, getRestrictionData(request)))
				{
					key.append(getKeyForRestriction(request, restriction));
				}
			}
			catch (final RestrictionEvaluationException e)
			{
				key.append(handleRestrictionEvaluationException(request, component, restriction, e));
			}
		}
	}

	protected String handleRestrictionEvaluationException(final HttpServletRequest request,
			final SimpleCMSComponentModel component, final AbstractRestrictionModel restriction,
			final RestrictionEvaluationException e) 
	{
		// No Restriction evaluator for this type of restriction
		return "";
	}

	protected StringBuilder getKeyForRestriction(final HttpServletRequest request, final AbstractRestrictionModel restriction)
	{
		final StringBuilder key = new StringBuilder();
		if (restriction instanceof CMSCatalogRestrictionModel)
		{
			processCMSCatalogRestriction((CMSCatalogRestrictionModel) restriction, key);
		}
		else if (restriction instanceof CMSCategoryRestrictionModel)
		{
			processCMSCategoryRestriction(request, key);
		}
		else if (restriction instanceof CMSInverseRestrictionModel)
		{
			final CMSInverseRestrictionModel inverseRestrictionModel = (CMSInverseRestrictionModel) restriction;
			key.append("not");
			key.append(getKeyForRestriction(request, inverseRestrictionModel.getOriginalRestriction()));
		}
		else if (restriction instanceof CMSProductRestrictionModel)
		{
			processCMSProductRestriction(request, key);
		}
		else if (restriction instanceof CMSTimeRestrictionModel)
		{
			final CMSTimeRestrictionModel timeRestrictionModel = (CMSTimeRestrictionModel) restriction;
			key.append(timeRestrictionModel.getActiveFrom());
			key.append(timeRestrictionModel.getActiveUntil());
		}
		else if (restriction instanceof CMSUserGroupRestrictionModel)
		{
			processCMSUserGroupRestriction((CMSUserGroupRestrictionModel) restriction, key);
		}
		else if (restriction instanceof CMSUserRestrictionModel)
		{
			processCMSUserRestriction((CMSUserRestrictionModel) restriction, key);
		}
		else if (restriction instanceof CMSUiExperienceRestrictionModel)
		{
			final CMSUiExperienceRestrictionModel uiExperienceRestrictionModel = (CMSUiExperienceRestrictionModel) restriction;
			key.append(uiExperienceRestrictionModel.getUiExperience());
		}
		return key;
	}

	protected void processCMSUserRestriction(final CMSUserRestrictionModel restriction, final StringBuilder key)
	{
		if (restriction.getUsers() != null)
		{
			for (final UserModel userModel : restriction.getUsers())
			{
				key.append(userModel.getUid());
			}
		}
	}

	protected void processCMSUserGroupRestriction(final CMSUserGroupRestrictionModel restriction, final StringBuilder key)
	{
		if (restriction.getUserGroups() != null)
		{
			for (final UserGroupModel userGroupModel : restriction.getUserGroups())
			{
				key.append(userGroupModel.getUid());
			}
		}
	}

	protected void processCMSCatalogRestriction(final CMSCatalogRestrictionModel restriction, final StringBuilder key)
	{
		final CMSCatalogRestrictionModel catalogRestrictionModel = restriction;
		if (catalogRestrictionModel.getCatalogs() != null)
		{
			for (final CatalogModel catalog : catalogRestrictionModel.getCatalogs())
			{
				key.append(catalog.getId());
			}
		}
	}

	protected void processCMSProductRestriction(final HttpServletRequest request, final StringBuilder key)
	{
		final RequestContextData context = getRequestContextData(request);
		if (context != null && context.getProduct() != null)
		{
			key.append(context.getProduct().getPk().getLongValueAsString());
		}
	}

	protected void processCMSCategoryRestriction(final HttpServletRequest request, final StringBuilder key)
	{
		final RequestContextData context = getRequestContextData(request);
		if (context != null && context.getCategory() != null)
		{
			key.append(context.getCategory().getPk().getLongValueAsString());
		}
	}

	/*
	 * Helper method to lookup RequestContextData from request.
	 */
	protected RequestContextData getRequestContextData(final HttpServletRequest request)
	{
		return SpringHelper.getSpringBean(request, "requestContextData", RequestContextData.class, true);
	}

	protected RestrictionData getRestrictionData(final HttpServletRequest request)
	{
		return (RestrictionData) getCmsPageContextService(request).getCmsPageRequestContextData(request).getRestrictionData();
	}

}
