/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.model.restrictions;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CMSUserGroupRestrictionModelTest extends ServicelayerBaseTest // NOPMD
{
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Mock
	private UserGroupModel userGroup1;
	@Mock
	private UserGroupModel userGroup2;
	@Mock
	private UserGroupModel userGroup3;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel#getDescription()}.
	 */
	@Test
	public void shouldHaveDynamicAttributeDescriptor()
	{
		//given
		final ComposedTypeModel type = typeService.getComposedTypeForClass(CMSUserGroupRestrictionModel.class);

		//when
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(type,
				CMSCategoryRestrictionModel.DESCRIPTION);

		//then
		assertThat(attributeDescriptor).isNotNull();
		assertThat(attributeDescriptor.getAttributeHandler()).isEqualTo("userGroupRestrictionDynamicDescription");
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel#getDescription()}.
	 *
	 */
	@Test
	public void shouldReturnRestrictionDescription()
	{
		//given
		final Collection<UserGroupModel> usersGroups = new ArrayList<UserGroupModel>();
		usersGroups.add(userGroup1);
		usersGroups.add(userGroup2);
		usersGroups.add(userGroup3);
		given(userGroup1.getUid()).willReturn("uid1");
		given(userGroup2.getUid()).willReturn("uid2");
		given(userGroup3.getUid()).willReturn("uid3");
		given(userGroup1.getLocName()).willReturn("loc name1");
		given(userGroup2.getLocName()).willReturn("loc name2");
		final CMSUserGroupRestrictionModel restriction = modelService.create(CMSUserGroupRestrictionModel.class);
		restriction.setUserGroups(usersGroups);

		//when
		final String description = restriction.getDescription();

		//then
		assertThat(description).isNotNull();
		assertThat(description).isEqualTo("Display for user groups: loc name1 (uid1); loc name2 (uid2); (uid3);");
	}
}
