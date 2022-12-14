/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.itemcomments.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.user.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Div;

import com.hybris.backoffice.widgets.itemcomments.ItemCommentsController;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;


@RunWith(MockitoJUnitRunner.class)
public class DefaultItemCommentsRendererTest
{

	@InjectMocks
	private DefaultItemCommentsRenderer defaultItemCommentsRenderer;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private WidgetModel widgetModel;
	@Spy
	private SimpleDateFormat simpleDateFormat;
	@Mock
	private CommentModel commentModel;
	@Mock
	private UserModel userModel;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private LabelService labelService;

	@Before
	public void setUp()
	{
		Mockito.lenient().when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		Mockito.lenient().when(widgetModel.getValue(ItemCommentsController.MODEL_DATE_FORMATTER, SimpleDateFormat.class))
				.thenReturn(simpleDateFormat);
		when(commentModel.getAuthor()).thenReturn(userModel);
		Mockito.lenient().when(commentModel.getText()).thenReturn("comment");
		Mockito.lenient().when(commentModel.getCreationtime()).thenReturn(new Date());
	}

	@Test
	public void renderComment()
	{
		//given
		final Div parent = new Div();
		//when
		defaultItemCommentsRenderer.render(parent, null, commentModel, null, widgetInstanceManager);
		//then
		assertThat(parent.getChildren().size()).isEqualTo(1);
	}

	@Test
	public void userShouldBeAbleToReadAuthor()
	{
		//given
		when(permissionFacade.canReadInstanceProperty(commentModel, CommentModel.AUTHOR)).thenReturn(true);
		when(permissionFacade.canReadInstance(userModel)).thenReturn(true);
		//when
		final boolean canRead = defaultItemCommentsRenderer.canReadAuthor(commentModel);
		//then
		assertThat(canRead).isTrue();
	}

	@Test
	public void userShouldNotBeAbleToReadAuthorNameWithoutPermission()
	{
		//given
		when(permissionFacade.canReadInstanceProperty(commentModel, CommentModel.AUTHOR)).thenReturn(true);
		Mockito.lenient().when(permissionFacade.canReadInstanceProperty(userModel, UserModel.DISPLAYNAME)).thenReturn(false);
		//when
		final boolean canRead = defaultItemCommentsRenderer.canReadAuthor(commentModel);
		//then
		assertThat(canRead).isFalse();
	}

	@Test
	public void userShouldBeAbleToReadText()
	{
		//given
		when(permissionFacade.canReadInstanceProperty(commentModel, CommentModel.TEXT)).thenReturn(true);
		//when
		final boolean canRead = defaultItemCommentsRenderer.canReadText(commentModel);
		//then
		assertThat(canRead).isTrue();
	}

	@Test
	public void userShouldBeAbleToReadCreationTime()
	{
		//given
		when(permissionFacade.canReadInstanceProperty(commentModel, CommentModel.CREATIONTIME)).thenReturn(true);
		//when
		final boolean canRead = defaultItemCommentsRenderer.canReadCreationTime(commentModel);
		//then
		assertThat(canRead).isTrue();
	}
}
