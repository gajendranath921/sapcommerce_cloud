/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckValue;
import de.hybris.platform.servicelayer.security.permissions.PermissionChecker;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.model.user.BackofficeRoleModel;


@RunWith(MockitoJUnitRunner.class)
public class BackofficePrincipalHierarchyCheckingStrategyTest
{

	@InjectMocks
	private BackofficePrincipalHierarchyCheckingStrategy testSubject;

	@Mock
	private UserService userService;
	@Mock
	private BackofficeRoleService backofficeRoleService;
	@Mock
	private BackofficeUserService backofficeUserService;
	@Mock
	private PermissionChecker permissionChecker;
	@Mock
	private UserModel user;

	private static final String PERMISSION_NAME = "permission";
	private static final String ROLE_UID = "role";


	@Test
	public void shouldConflictOnDirectGroupAndIndirectActiveRole()
	{
		// given
		final UserGroupModel directGroup1 = mock(UserGroupModel.class);
		final UserGroupModel directGroup2 = mock(UserGroupModel.class);
		final BackofficeRoleModel indirectRole = mock(BackofficeRoleModel.class);
		final Set<PrincipalGroupModel> directGroups = Sets.newHashSet(directGroup1, directGroup2);
		when(indirectRole.getUid()).thenReturn(ROLE_UID);
		when(user.getGroups()).thenReturn(directGroups);
		Mockito.lenient().when(user.getAllGroups()).thenReturn(Sets.newHashSet(directGroup1, directGroup2, indirectRole));
		when(directGroup2.getGroups()).thenReturn(Collections.singleton(indirectRole));
		Mockito.lenient().when(directGroup2.getAllGroups()).thenReturn(Collections.singleton(indirectRole));

		when(backofficeRoleService.shouldTreatRolesAsGroups()).thenReturn(false);
		Mockito.lenient().when(backofficeRoleService.isActiveRole(indirectRole.getUid())).thenReturn(true);
		when(backofficeRoleService.filterOutRolePrincipals(directGroups)).thenReturn(directGroups);
		when(backofficeRoleService.getActiveRoleModel()).thenReturn(Optional.of(indirectRole));
		when(backofficeUserService.isCurrentUser(user)).thenReturn(true);
		when(userService.isAdmin(user)).thenReturn(false);
		when(permissionChecker.checkPermission(user, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(directGroup1, PERMISSION_NAME)).thenReturn(PermissionCheckValue.DENIED);
		when(permissionChecker.checkPermission(directGroup2, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(indirectRole, PERMISSION_NAME)).thenReturn(PermissionCheckValue.ALLOWED);

		// when
		final PermissionCheckValue result = testSubject.checkPermissionsForPrincipalHierarchy(permissionChecker, user,
				PERMISSION_NAME);

		// then
		assertThat(result).isEqualTo(PermissionCheckValue.CONFLICTING);
	}

	@Test
	public void shouldIgnoreInactiveRole()
	{
		// given
		final UserGroupModel directGroup1 = mock(UserGroupModel.class);
		final UserGroupModel directGroup2 = mock(UserGroupModel.class);
		final BackofficeRoleModel directRole = mock(BackofficeRoleModel.class);
		final Set<PrincipalGroupModel> directGroups = Sets.newHashSet(directGroup1, directGroup2, directRole);
		when(directRole.getUid()).thenReturn(ROLE_UID);
		when(user.getGroups()).thenReturn(directGroups);
		Mockito.lenient().when(user.getAllGroups()).thenReturn(directGroups);

		when(backofficeRoleService.shouldTreatRolesAsGroups()).thenReturn(false);
		Mockito.lenient().when(backofficeRoleService.isActiveRole(directRole.getUid())).thenReturn(false);
		when(backofficeRoleService.filterOutRolePrincipals(directGroups)).thenReturn(Sets.newHashSet(directGroup1, directGroup2));
		when(backofficeRoleService.getActiveRoleModel()).thenReturn(Optional.empty());
		when(backofficeUserService.isCurrentUser(user)).thenReturn(true);
		when(userService.isAdmin(user)).thenReturn(false);
		when(permissionChecker.checkPermission(user, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(directGroup1, PERMISSION_NAME)).thenReturn(PermissionCheckValue.DENIED);
		when(permissionChecker.checkPermission(directGroup2, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		Mockito.lenient().when(permissionChecker.checkPermission(directRole, PERMISSION_NAME)).thenReturn(PermissionCheckValue.ALLOWED);

		// when
		final PermissionCheckValue result = testSubject.checkPermissionsForPrincipalHierarchy(permissionChecker, user,
				PERMISSION_NAME);

		// then
		assertThat(result).isEqualTo(PermissionCheckValue.DENIED);
	}

	@Test
	public void shouldConflictOnActiveRoleHierarchy()
	{
		// given
		final UserGroupModel indirectGroup = mock(UserGroupModel.class);
		final BackofficeRoleModel directRole = mock(BackofficeRoleModel.class);
		final BackofficeRoleModel indirectRole = mock(BackofficeRoleModel.class);
		final Set<PrincipalGroupModel> directGroups = Collections.singleton(directRole);
		when(directRole.getUid()).thenReturn(ROLE_UID);
		when(user.getGroups()).thenReturn(directGroups);
		Mockito.lenient().when(user.getAllGroups()).thenReturn(Sets.newHashSet(indirectGroup, directRole, indirectRole));
		when(directRole.getGroups()).thenReturn(Sets.newHashSet(indirectGroup, indirectRole));
		Mockito.lenient().when(directRole.getAllGroups()).thenReturn(Sets.newHashSet(indirectGroup, indirectRole));

		when(backofficeRoleService.shouldTreatRolesAsGroups()).thenReturn(false);
		when(backofficeRoleService.isActiveRole(directRole.getUid())).thenReturn(true);
		when(backofficeRoleService.filterOutRolePrincipals(directGroups)).thenReturn(new HashSet<>());
		when(backofficeRoleService.getActiveRoleModel()).thenReturn(Optional.of(directRole));
		when(backofficeUserService.isCurrentUser(user)).thenReturn(true);
		when(userService.isAdmin(user)).thenReturn(false);
		when(permissionChecker.checkPermission(user, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(directRole, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(indirectGroup, PERMISSION_NAME)).thenReturn(PermissionCheckValue.DENIED);
		when(permissionChecker.checkPermission(indirectRole, PERMISSION_NAME)).thenReturn(PermissionCheckValue.ALLOWED);

		// when
		final PermissionCheckValue result = testSubject.checkPermissionsForPrincipalHierarchy(permissionChecker, user,
				PERMISSION_NAME);

		// then
		assertThat(result).isEqualTo(PermissionCheckValue.CONFLICTING);
	}

	@Test
	public void shouldConflictOnInactiveRoleInLegacyMode()
	{
		// given
		final UserGroupModel directGroup1 = mock(UserGroupModel.class);
		final UserGroupModel directGroup2 = mock(UserGroupModel.class);
		final BackofficeRoleModel directRole = mock(BackofficeRoleModel.class);
		final Set<PrincipalGroupModel> directGroups = Sets.newHashSet(directGroup1, directGroup2, directRole);
		Mockito.lenient().when(directRole.getUid()).thenReturn(ROLE_UID);
		when(user.getGroups()).thenReturn(directGroups);
		Mockito.lenient().when(user.getAllGroups()).thenReturn(directGroups);

		when(backofficeRoleService.shouldTreatRolesAsGroups()).thenReturn(true);
		Mockito.lenient().when(backofficeUserService.isCurrentUser(user)).thenReturn(true);
		when(userService.isAdmin(user)).thenReturn(false);
		when(permissionChecker.checkPermission(user, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(directGroup1, PERMISSION_NAME)).thenReturn(PermissionCheckValue.DENIED);
		when(permissionChecker.checkPermission(directGroup2, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(directRole, PERMISSION_NAME)).thenReturn(PermissionCheckValue.ALLOWED);

		// when
		final PermissionCheckValue result = testSubject.checkPermissionsForPrincipalHierarchy(permissionChecker, user,
				PERMISSION_NAME);

		// then
		assertThat(result).isEqualTo(PermissionCheckValue.CONFLICTING);
	}

	@Test
	public void shouldIgnoreAllRolesIfUserIsNotCurrentLoginUser()
	{
		// given
		final UserGroupModel directGroup1 = mock(UserGroupModel.class);
		final BackofficeRoleModel directRole1 = mock(BackofficeRoleModel.class);
		final BackofficeRoleModel directRole2 = mock(BackofficeRoleModel.class);
		final Set<PrincipalGroupModel> directGroups = Sets.newHashSet(directGroup1, directRole1, directRole2);
		when(directRole1.getUid()).thenReturn("role1");
		when(directRole2.getUid()).thenReturn("role2");
		when(user.getGroups()).thenReturn(directGroups);

		when(backofficeUserService.isCurrentUser(user)).thenReturn(false);

		Mockito.lenient().when(backofficeRoleService.isActiveRole(directRole1.getUid())).thenReturn(false);
		Mockito.lenient().when(backofficeRoleService.isActiveRole(directRole2.getUid())).thenReturn(true);
		when(backofficeRoleService.shouldTreatRolesAsGroups()).thenReturn(false);
		when(backofficeRoleService.filterOutRolePrincipals(directGroups)).thenReturn(Sets.newHashSet(directGroup1));

		when(userService.isAdmin(user)).thenReturn(false);
		when(permissionChecker.checkPermission(user, PERMISSION_NAME)).thenReturn(PermissionCheckValue.NOT_DEFINED);
		when(permissionChecker.checkPermission(directGroup1, PERMISSION_NAME)).thenReturn(PermissionCheckValue.DENIED);
		Mockito.lenient().when(permissionChecker.checkPermission(directRole1, PERMISSION_NAME)).thenReturn(PermissionCheckValue.ALLOWED);
		Mockito.lenient().when(permissionChecker.checkPermission(directRole2, PERMISSION_NAME)).thenReturn(PermissionCheckValue.ALLOWED);

		// when
		final PermissionCheckValue result = testSubject.checkPermissionsForPrincipalHierarchy(permissionChecker, user,
				PERMISSION_NAME);

		// then
		assertThat(result).isEqualTo(PermissionCheckValue.DENIED);
	}



}
