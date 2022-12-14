/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.core;

import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericConditionList;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSearchField;
import de.hybris.platform.core.GenericSearchOrderBy;
import de.hybris.platform.core.Operator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.genericsearch.GenericSearchQuery;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import com.hybris.backoffice.cockpitng.dataaccess.facades.search.DefaultPlatformFieldSearchFacadeStrategy;
import com.hybris.cockpitng.search.data.SearchQueryData;


/**
 * FieldSearchFacadeStrategy that filters out system records for yForms.
 */
public class DefaultYFormsFieldSearchFacadeStrategy<T extends ItemModel> extends DefaultPlatformFieldSearchFacadeStrategy<T>
{
	private final static Logger LOG = Logger.getLogger(DefaultYFormsFieldSearchFacadeStrategy.class);

	private Set<String> types;
	private Set<String> rolesNotAllowed;

	private UserService userService;

	@Override
	public boolean canHandle(final String typeCode)
	{
		return types.contains(typeCode);
	}

	@Override
	protected GenericSearchQuery buildQuery(final SearchQueryData searchQueryData)
	{
		final GenericSearchQuery searchQuery = super.buildQuery(searchQueryData);

		if (!shouldFilterOutSystemRecords())
		{
			return searchQuery;
		}

		final GenericQuery originalQuery = searchQuery.getQuery();
		final String typeCode = searchQueryData.getSearchType();

		LOG.debug("Filtering out system records for [" + typeCode + "]");

		final GenericQuery newQuery = new GenericQuery(typeCode);

		final GenericCondition systemCondition = GenericCondition
				.createConditionForValueComparison(new GenericSearchField("system"), Operator.EQUAL, Integer.valueOf(0));

		GenericCondition allConditions = originalQuery.getCondition();
		allConditions = allConditions != null ? GenericConditionList.and(systemCondition, allConditions) : systemCondition;

		newQuery.addCondition(allConditions);

		final Collection<GenericSearchOrderBy> orderByList = originalQuery.getOrderByList();
		for (final GenericSearchOrderBy orderBy : orderByList)
		{
			newQuery.addOrderBy(orderBy);
		}

		newQuery.setTypeExclusive(originalQuery.isTypeExclusive());

		return new GenericSearchQuery(newQuery);
	}

	/**
	 * Decides if the user has the rights to see system records.
	 */
	private boolean shouldFilterOutSystemRecords()
	{
		final UserModel userModel = userService.getCurrentUser();
		if (userService.isAdmin(userModel))
		{
			LOG.debug("User has admin rights, system records will be shown");
			return false;
		}

		return YFormsBackofficeHelper.isUserInNotAllowedRoles(rolesNotAllowed, userModel);
	}


	@Required
	public void setTypes(final String types)
	{
		this.types = StringUtils.commaDelimitedListToSet(types);
	}

	@Required
	public void setRolesNotAllowed(final String rolesNotAllowed)
	{
		this.rolesNotAllowed = StringUtils.commaDelimitedListToSet(rolesNotAllowed);
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
