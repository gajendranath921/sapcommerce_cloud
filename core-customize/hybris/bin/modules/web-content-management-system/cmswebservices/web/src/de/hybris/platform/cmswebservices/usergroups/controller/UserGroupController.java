/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.usergroups.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserGroupData;
import de.hybris.platform.cmsfacades.usergroups.UserGroupFacade;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.dto.UserGroupListWsDTO;
import de.hybris.platform.cmswebservices.dto.UserGroupWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller to retrieve and search for User Groups.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/usergroups")
@Api(tags = "user groups")
public class UserGroupController
{
	@Resource
	private UserGroupFacade cmsUserGroupFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@RequestMapping(value = "/{userGroupId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Gets user group by id.", notes = "Retrieves a specific user group instance that matches the given id.",
					nickname = "getUserGroupById")
	@ApiResponses({
			@ApiResponse(code = 400, message = "When the user group was not found (CMSItemNotFoundException) or when there was problem during conversion (ConversionException)."),
			@ApiResponse(code = 200, message = "UserGroupWsDTO", response = UserGroupWsDTO.class)
	})
	public UserGroupWsDTO getUserGroupById(
			@ApiParam(value = "The unique identifier of the user group", required = true) @PathVariable final String userGroupId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getCmsUserGroupFacade().getUserGroupById(userGroupId), UserGroupWsDTO.class);
	}

	@RequestMapping(method = RequestMethod.GET, params = { "pageSize" })
	@ResponseBody
	@ApiOperation(value = "Finds user group by text.", notes = "Retrieves a list of available user groups using a free text search field.",
					nickname = "getUserGroupsByText")
	@ApiResponses({
			@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of UserGroupData; never null", response = UserGroupListWsDTO.class)
	})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "The maximum number of elements in the result list.", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "currentPage", value = "The requested page number", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "The string field the results will be sorted with", required = false, dataType = "string", paramType = "query")
	})
	public UserGroupListWsDTO findUserGroupsByText(
			@ApiParam(value = "The string value on which products will be filtered", required = false) @RequestParam(required = false) final String mask,
			@ApiParam(value = "PageableWsDTO", required = true) @ModelAttribute final PageableWsDTO pageableInfo)
	{
		final SearchResult<UserGroupData> userGroupSearchResult = getCmsUserGroupFacade().findUserGroups(mask,
				Optional.of(pageableInfo).map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class)).get());

		final UserGroupListWsDTO userGroups = new UserGroupListWsDTO();
		userGroups.setUserGroups(userGroupSearchResult //
				.getResult() //
				.stream() //
				.map(productData -> getDataMapper().map(productData, UserGroupWsDTO.class)) //
				.collect(Collectors.toList()));
		userGroups.setPagination(getWebPaginationUtils().buildPagination(userGroupSearchResult));
		return userGroups;
	}

	protected UserGroupFacade getCmsUserGroupFacade()
	{
		return cmsUserGroupFacade;
	}

	public void setCmsUserGroupFacade(final UserGroupFacade cmsUserGroupFacade)
	{
		this.cmsUserGroupFacade = cmsUserGroupFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}
}
