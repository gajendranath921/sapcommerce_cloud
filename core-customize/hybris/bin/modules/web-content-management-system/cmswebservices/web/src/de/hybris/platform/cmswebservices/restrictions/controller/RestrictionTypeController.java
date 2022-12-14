/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.restrictions.controller;

import de.hybris.platform.cmsfacades.restrictions.RestrictionFacade;
import de.hybris.platform.cmswebservices.data.RestrictionTypeData;
import de.hybris.platform.cmswebservices.data.RestrictionTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;


/**
 * Controller to get restriction types.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/restrictiontypes")
@Api(tags = "restriction types")
public class RestrictionTypeController
{

	@Resource
	private RestrictionFacade restritionFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds all restriction types.", notes = "Retrieves a list of available restriction types.",
					nickname = "getAllRestrictionTypes")
	@ApiResponses({
			@ApiResponse(code = 200, message = "List of restriction types", response = RestrictionTypeListData.class)
	})
	public RestrictionTypeListData findAllRestrictionTypes()
	{
		final List<RestrictionTypeData> restrictionTypes = getDataMapper()
				.mapAsList(getRestritionFacade().findAllRestrictionTypes(), RestrictionTypeData.class, null);

		final RestrictionTypeListData restrictionTypeListData = new RestrictionTypeListData();
		restrictionTypeListData.setRestrictionTypes(restrictionTypes);
		return restrictionTypeListData;
	}

	protected RestrictionFacade getRestritionFacade()
	{
		return restritionFacade;
	}

	public void setRestritionFacade(final RestrictionFacade restritionFacade)
	{
		this.restritionFacade = restritionFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

}
