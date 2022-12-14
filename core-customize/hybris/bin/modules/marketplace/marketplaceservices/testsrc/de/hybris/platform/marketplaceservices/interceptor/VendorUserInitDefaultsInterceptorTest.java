/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.interceptor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class VendorUserInitDefaultsInterceptorTest
{
	private static final String VENDERADMINGROUP_UID = "vendoradministratorgroup";
	private static final String MERCHANTOPRATORGROUP_UID = "merchantoperatorgroup";

	private VendorUserInitDefaultsInterceptor interceptor;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Mock
	private InterceptorContext ctx;

	private VendorUserModel vendorUser;


	@Before
	public void prepare()
	{
		interceptor = new VendorUserInitDefaultsInterceptor();
		interceptor.setUserService(userService);
		interceptor.setModelService(modelService);

		vendorUser = new VendorUserModel();
	}

	@Test
	public void testOnInitDefaultsByAdministrator() throws InterceptorException
	{
		final PrincipalGroupModel vendorAdministratorGroup = new PrincipalGroupModel();
		vendorAdministratorGroup.setUid(VENDERADMINGROUP_UID);
		final UserModel currentUser = mock(UserModel.class);

		given(userService.getCurrentUser()).willReturn(currentUser);
		given(currentUser.getAllGroups()).willReturn(Collections.<PrincipalGroupModel> singleton(vendorAdministratorGroup));

		interceptor.onInitDefaults(vendorUser, ctx);
		Assert.assertNull(vendorUser.getGroups());
	}

	@Test
	public void testOnInitDefaultsByMechantOperator() throws InterceptorException
	{
		final UserModel currentUser = mock(UserModel.class);
		given(userService.getCurrentUser()).willReturn(currentUser);

		final PrincipalGroupModel mechantOperatorPrincipalGroup = new PrincipalGroupModel();
		mechantOperatorPrincipalGroup.setUid(MERCHANTOPRATORGROUP_UID);
		given(currentUser.getAllGroups()).willReturn(Collections.<PrincipalGroupModel> singleton(mechantOperatorPrincipalGroup));

		final UserGroupModel mechantOperatorGroup = mock(UserGroupModel.class);
		given(userService.getUserGroupForUID(anyString())).willReturn(mechantOperatorGroup);

		interceptor.onInitDefaults(vendorUser, ctx);
		vendorUser.getGroups().forEach(group -> Assert.assertEquals(mechantOperatorGroup, group));
	}
}
