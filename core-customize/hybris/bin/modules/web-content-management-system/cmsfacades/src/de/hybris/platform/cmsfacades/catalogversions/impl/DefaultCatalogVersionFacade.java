/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.catalogversions.impl;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.catalogversions.CatalogVersionFacade;
import de.hybris.platform.cmsfacades.catalogversions.service.PageDisplayConditionService;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Objects;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import static java.util.stream.Collectors.toList;


/**
 * Facade interface which deals with methods related to catalog version operations.
 */
public class DefaultCatalogVersionFacade implements CatalogVersionFacade
{
	private CatalogVersionService catalogVersionService;
	private Converter<CatalogVersionModel, CatalogVersionData> cmsCatalogVersionConverter;
	private PageDisplayConditionService pageDisplayConditionService;
	private CMSCatalogVersionService cmsCatalogVersionService;
	private CatalogService catalogService;
	private CMSAdminSiteService cmsAdminSiteService;
	private UserService userService;
	private ModelService modelService;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionDisabler;

	@Override
	public CatalogVersionData getCatalogVersion(final String catalogId, final String versionId) throws CMSItemNotFoundException
	{
		final CatalogVersionModel catalogVersionModel = getCatalogVersionService().getCatalogVersion(catalogId, versionId);
		// populate basic information : catalog id, name and version
		final CatalogVersionData catalogVersion = getCmsCatalogVersionConverter().convert(catalogVersionModel);

		if (Objects.isNull(catalogVersion))
		{
			throw new CMSItemNotFoundException("Cannot find catalog version");
		}

		// find all page display options per page type
		catalogVersion.setPageDisplayConditions(getPageDisplayConditionService().getDisplayConditions());

		return catalogVersion;
	}

	@Override
	public List<CatalogVersionData> getWritableContentCatalogVersionTargets(final String siteId, final String catalogId,
			final String versionId)
	{
		CatalogModel catalogModel = getCatalogService().getCatalogForId(catalogId);
		final CMSSiteModel siteModel = getSessionSearchRestrictionDisabler()
				.execute(() -> {
					CMSSiteModel cmsSiteModel = getCmsAdminSiteService().getSiteForId(siteId);
					modelService.refresh(cmsSiteModel);
					cmsSiteModel.getContentCatalogs();
					return cmsSiteModel;
				});
		PrincipalModel principalModel = getUserService().getCurrentUser();
		CatalogVersionModel catalogVersionModel = getCatalogVersionService().getCatalogVersion(catalogId, versionId);
		List<CatalogVersionModel> catalogVersionModels = null;

		if (catalogVersionModel.getActive())
		{
			catalogVersionModels = getCmsCatalogVersionService()
					.getWritableChildContentCatalogVersions(principalModel, siteModel, catalogModel);
		}
		else
		{
			catalogVersionModels = getCmsCatalogVersionService()
					.getWritableContentCatalogVersions(principalModel, catalogModel);
		}

		return convertToListData(catalogVersionModels);
	}

	protected List<CatalogVersionData> convertToListData(List<CatalogVersionModel> catalogVersionModels)
	{
		return catalogVersionModels.stream()
				.map(versionModel ->
						getCmsCatalogVersionConverter().convert(versionModel)).collect(toList());
	}

	protected Converter<CatalogVersionModel, CatalogVersionData> getCmsCatalogVersionConverter()
	{
		return cmsCatalogVersionConverter;
	}

	@Required
	public void setCmsCatalogVersionConverter(final Converter<CatalogVersionModel, CatalogVersionData> cmsCatalogVersionConverter)
	{
		this.cmsCatalogVersionConverter = cmsCatalogVersionConverter;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected PageDisplayConditionService getPageDisplayConditionService()
	{
		return pageDisplayConditionService;
	}

	@Required
	public void setPageDisplayConditionService(final PageDisplayConditionService pageDisplayConditionService)
	{
		this.pageDisplayConditionService = pageDisplayConditionService;
	}

	protected CMSCatalogVersionService getCmsCatalogVersionService()
	{
		return cmsCatalogVersionService;
	}

	@Required
	public void setCmsCatalogVersionService(CMSCatalogVersionService cmsCatalogVersionService)
	{
		this.cmsCatalogVersionService = cmsCatalogVersionService;
	}

	protected CatalogService getCatalogService()
	{
		return catalogService;
	}

	@Required
	public void setCatalogService(CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	public SessionSearchRestrictionsDisabler getSessionSearchRestrictionDisabler()
	{
		return sessionSearchRestrictionDisabler;
	}

	public void setSessionSearchRestrictionDisabler(
			SessionSearchRestrictionsDisabler sessionSearchRestrictionDisabler)
	{
		this.sessionSearchRestrictionDisabler = sessionSearchRestrictionDisabler;
	}
}
