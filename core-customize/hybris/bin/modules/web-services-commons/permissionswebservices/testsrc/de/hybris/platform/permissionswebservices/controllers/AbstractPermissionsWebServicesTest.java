/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionswebservices.controllers;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;


public class AbstractPermissionsWebServicesTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private PermissionManagementService permissionManagementService;

	protected void insertGlobalPermission(final String principalId, final String permission)
	{
		final PrincipalModel example = new PrincipalModel();
		example.setUid(principalId);
		final PrincipalModel principal = flexibleSearchService.getModelByExample(example);
		permissionManagementService.addGlobalPermission(new PermissionAssignment(permission, principal));
	}

	protected static boolean isGetPermissionsTestEnabled()
	{
		return Config.getBoolean("permissionswebservices.enableGetPermissionsTest", false);
	}
}
