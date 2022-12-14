/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.usergroups.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserGroupData;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultUserGroupFacadeTest
{

	private static final String VALID_ID = "valid-id";
	private static final String INVALID_ID = "invalid-id";

	@InjectMocks
	private DefaultUserGroupFacade userGroupFacade;

	@Mock
	private UserService userService;
	@Mock
	private Converter<UserGroupModel, UserGroupData> userGroupDataConverter;

	@Mock
	private UserGroupModel userGroupModel;

	@Test
	public void shouldGetOneUserGroupById() throws CMSItemNotFoundException
	{
		when(userService.getUserGroupForUID(VALID_ID)).thenReturn(userGroupModel);

		userGroupFacade.getUserGroupById(VALID_ID);

		verify(userService).getUserGroupForUID(VALID_ID);
		verify(userGroupDataConverter).convert(userGroupModel);
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailGetOneUserGroupById() throws CMSItemNotFoundException
	{
		when(userService.getUserGroupForUID(INVALID_ID)).thenThrow(new UnknownIdentifierException("not found"));

		userGroupFacade.getUserGroupById(INVALID_ID);
	}

}
