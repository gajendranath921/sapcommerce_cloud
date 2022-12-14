/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentSlotDao;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSAdminContentSlotServiceTest
{

	private static final int MIN_CONTENT_SLOT_RELATIONS = 1;
	private static final int MAX_CONTENT_SLOT_RELATIONS = 5;

	private static final String CONTENT_SLOT_POSITION = "contentSlotPosition";

	private static final String SLOT_1_ID = "SomeSlotUID1";
	private static final String SLOT_2_ID = "SomeSlotUID2";
	private static final String SLOT_3_ID = "SomeSlotUID3";

	@Captor
	private ArgumentCaptor<ContentSlotForPageModel> savedContentSlotForPageCaptor;

	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private ModelService modelService;

	@Mock
	private CMSCatalogVersionService cmsCatalogVersionService;

	@Mock
	private PermissionCRUDService permissionCRUDService;

	@InjectMocks
	@Spy
	private DefaultCMSAdminContentSlotService cmsAdminContentSlotService;

	@Mock
	private DefaultCMSAdminComponentService cmsAdminComponentService;

	@Mock
	private CMSContentSlotDao cmsContentSlotDao;

	@Mock
	private CMSDataFactory cmsDataFactory;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private AbstractPageModel page;

	@Mock
	private PageTemplateModel template;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CatalogVersionModel catalogVersionParent;

	private List<CatalogVersionModel> hierarchyOfCatalogVersions = Arrays.asList(catalogVersion, catalogVersionParent);

	@Mock
	private SessionService sessionService;

	@Mock
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private CatalogLevelService catalogLevelService;

	@Mock
	private CMSSiteModel cmsSite;

	@Mock
	private ContentCatalogModel contentCatalog;

	@Mock
	private Date from;

	@Mock
	private Date to;

	@Mock
	private AbstractCMSComponentModel componentModel;

	@Mock
	private ContentSlotModel slotModel1;

	@Mock
	private ContentSlotModel slotModel2;

	@Mock
	private ContentSlotModel slotModel3;

	@Mock
	private CMSPageService cmsPageService;

	private final ContentSlotModel contentSlotModel = new ContentSlotModel();

	private final ContentSlotForPageModel contentSlotForPageModel = new ContentSlotForPageModel();

	/**
	 * Generates a list of {@value MIN_CONTENT_SLOT_RELATIONS} to {@value MAX_CONTENT_SLOT_RELATIONS} content slot
	 * relation model mocks for the page.
	 *
	 * @return the list content slot relation model mocks for the page
	 */
	protected List<ContentSlotForPageModel> generateContentSlotForPageModelMocks()
	{
		final List<ContentSlotForPageModel> contentSlotForPageModels = new ArrayList<>();

		final int contentSlotRelationCount = ThreadLocalRandom.current().nextInt(MIN_CONTENT_SLOT_RELATIONS,
				MAX_CONTENT_SLOT_RELATIONS);

		for (int i = 0; i < contentSlotRelationCount; i++)
		{
			final ContentSlotForPageModel contentSlotForPageModel = new ContentSlotForPageModel();
			contentSlotForPageModel.setPosition(CONTENT_SLOT_POSITION + i);

			final ContentSlotData contentSlotData = mock(ContentSlotData.class);
			when(contentSlotData.getPosition()).thenReturn(CONTENT_SLOT_POSITION + i);

			when(cmsDataFactory.createContentSlotData(contentSlotForPageModel)).thenReturn(contentSlotData);

			contentSlotForPageModels.add(contentSlotForPageModel);
		}

		return contentSlotForPageModels;
	}

	/**
	 * Generates a list of {@value MIN_CONTENT_SLOT_RELATIONS} to {@value MAX_CONTENT_SLOT_RELATIONS} content slot
	 * relation model mocks for the page template.
	 *
	 * @param withPositions
	 *           true if the template slots should have a position parameter to test the filtering out of custom content
	 *           slots on the page
	 * @return the list content slot relation model mocks for the page template
	 */
	protected List<ContentSlotForTemplateModel> generateContentSlotForTemplateModelMocks(final boolean isOverlapExpected)
	{
		final List<ContentSlotForTemplateModel> contentSlotForTemplateModels = new ArrayList<>();

		final int contentSlotRelationCount = ThreadLocalRandom.current().nextInt(MIN_CONTENT_SLOT_RELATIONS,
				MAX_CONTENT_SLOT_RELATIONS);

		for (int i = 0; i < contentSlotRelationCount; i++)
		{
			final ContentSlotForTemplateModel contentSlotForTemplateModel = mock(ContentSlotForTemplateModel.class);
			final String position = isOverlapExpected ? CONTENT_SLOT_POSITION + i : "templateSlotPosition" + i;
			when(contentSlotForTemplateModel.getPosition()).thenReturn(position);

			final ContentSlotData contentSlotData = mock(ContentSlotData.class);
			when(contentSlotData.getPosition()).thenReturn(CONTENT_SLOT_POSITION + i);

			when(cmsDataFactory.createContentSlotData(page, contentSlotForTemplateModel)).thenReturn(contentSlotData);

			contentSlotForTemplateModels.add(contentSlotForTemplateModel);
		}

		return contentSlotForTemplateModels;
	}

	/**
	 * Calculates how many content slots should be returned, based on the number of content slots on the page, the
	 * template and if an overlap of slots (custom page slots replacing template slots) is expected.
	 *
	 * @param contentSlotsForPageCount
	 *           the number of content slots on the page
	 * @param contentSlotsForTemplateCount
	 *           the number of content slots on the template
	 * @param isOverlapExpected
	 *           true if there are custom content slots on the page that override the slot on the template, false
	 *           otherwise
	 * @return
	 */
	protected int calculateNumberExpectedContentSlotsForPage(final int contentSlotsForPageCount,
			final int contentSlotsForTemplateCount, final boolean isOverlapExpected)
	{
		int expectedContentSlotsForPageCount;

		if (isOverlapExpected)
		{
			expectedContentSlotsForPageCount = contentSlotsForTemplateCount > contentSlotsForPageCount ? contentSlotsForTemplateCount
					: contentSlotsForPageCount;
		}
		else
		{
			expectedContentSlotsForPageCount = contentSlotsForPageCount + contentSlotsForTemplateCount;
		}

		return expectedContentSlotsForPageCount;
	}

	@Before
	public void setUp()
	{
		when(keyGenerator.generate()).thenReturn("generatedKey");

		when(page.getUid()).thenReturn("mypage$uid");
		when(page.getCatalogVersion()).thenReturn(catalogVersion);
		when(page.getMasterTemplate()).thenReturn(template);

		when(cmsDataFactory.createContentSlotData(any(ContentSlotForPageModel.class))).thenReturn(mock(ContentSlotData.class));
		when(cmsDataFactory.createContentSlotData(any(AbstractPageModel.class), any(ContentSlotForTemplateModel.class)))
				.thenReturn(mock(ContentSlotData.class));

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getBoolean(anyString())).thenReturn(Boolean.FALSE);

		when(cmsAdminSiteService.getActiveSite()).thenReturn(cmsSite);
		when(cmsSite.getContentCatalogs()).thenReturn(Arrays.asList(contentCatalog));
		when(catalogLevelService.getLevelCatalogVersions(any(), any())).thenReturn(hierarchyOfCatalogVersions);

		when(modelService.create(ContentSlotModel.class)).thenReturn(contentSlotModel);
		when(modelService.create(ContentSlotForPageModel.class)).thenReturn(contentSlotForPageModel);

		when(slotModel1.getUid()).thenReturn(SLOT_1_ID);
		when(slotModel2.getUid()).thenReturn(SLOT_2_ID);
		when(slotModel3.getUid()).thenReturn(SLOT_3_ID);

		when(cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersion, cmsSite)).thenReturn(hierarchyOfCatalogVersions);
	}

	@Test
	public void willCreateAndAssignSlotsToThePositionsNotBusyWithSharedSlots()
	{
		final ContentSlotModel returnValue = cmsAdminContentSlotService.createContentSlot(page, null, "name1", "position1", true,
				from, to);

		assertThat(returnValue, is(contentSlotModel));

		assertThat(contentSlotModel,
				allOf(hasProperty("uid", is("position1Slot-mypage-uid")), hasProperty("name", is("name1")),
						hasProperty("active", is(true)), hasProperty("activeFrom", is(from)), hasProperty("activeUntil", is(to)),
						hasProperty("catalogVersion", is(catalogVersion))));

		assertThat(contentSlotForPageModel,
				allOf(hasProperty("uid", is("contentSlotForPage-generatedKey")), hasProperty("catalogVersion", is(catalogVersion)),
						hasProperty("position", is("position1")), hasProperty("page", is(page)),
						hasProperty("contentSlot", is(contentSlotModel))));

	}

	@Test
	public void willReturnOnlyContentSlotsForPage()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = generateContentSlotForPageModelMocks();
		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page,
				false);

		// Assert
		assertEquals(expectedContentSlotsForPage.size(), actualContentSlotsForPage.size());

		verify(cmsContentSlotDao, never()).findContentSlotRelationsByPageTemplateAndCatalogVersions(template, hierarchyOfCatalogVersions);
	}

	@Test
	public void willReturnContentSlotsForPageAndForMasterTemplate()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = generateContentSlotForPageModelMocks();
		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		final List<ContentSlotForTemplateModel> expectedContentSlotsForTemplate = generateContentSlotForTemplateModelMocks(false);
		when(cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(template, hierarchyOfCatalogVersions)).thenReturn(expectedContentSlotsForTemplate);
		when(cmsPageService.getChildSlotForTemplateAtSamePosition(expectedContentSlotsForTemplate)).thenReturn(expectedContentSlotsForTemplate);

		final int expectedContentSlotsForPageCount = calculateNumberExpectedContentSlotsForPage(expectedContentSlotsForPage.size(),
				expectedContentSlotsForTemplate.size(), false);
		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page, true);

		// Assert
		assertEquals(expectedContentSlotsForPageCount, actualContentSlotsForPage.size());
	}

	@Test
	public void willReturnCustomContentSlotsInTemplateSlotPositions()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = generateContentSlotForPageModelMocks();
		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		final List<ContentSlotForTemplateModel> expectedContentSlotsForTemplate = generateContentSlotForTemplateModelMocks(true);
		when(cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(template, hierarchyOfCatalogVersions))
				.thenReturn(expectedContentSlotsForTemplate);
		when(cmsPageService.getChildSlotForTemplateAtSamePosition(expectedContentSlotsForTemplate)).thenReturn(expectedContentSlotsForTemplate);

		final int expectedContentSlotsForPageCount = calculateNumberExpectedContentSlotsForPage(expectedContentSlotsForPage.size(),
				expectedContentSlotsForTemplate.size(), true);

		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page, true);

		// Assert
		assertEquals(expectedContentSlotsForPageCount, actualContentSlotsForPage.size());
	}

	@Test
	public void willReturnCustomContentSlotsInTemplateSlotPositionsAndSetOverrideToTrue()
	{
		// GIVEN
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = new ArrayList<>();
		final ContentSlotForPageModel contentSlotForPageModel = mock(ContentSlotForPageModel.class);
		when(contentSlotForPageModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);

		final ContentSlotData contentSlotData = mock(ContentSlotData.class);
		when(contentSlotData.getPosition()).thenReturn(CONTENT_SLOT_POSITION);

		when(cmsDataFactory.createContentSlotData(contentSlotForPageModel)).thenReturn(contentSlotData);

		expectedContentSlotsForPage.add(contentSlotForPageModel);

		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class))).thenReturn(expectedContentSlotsForPage);

		final List<ContentSlotForTemplateModel> contentSlotsForTemplateRelations = new ArrayList<>();
		final ContentSlotForTemplateModel contentSlotForTemplateModel = mock(ContentSlotForTemplateModel.class);
		when(contentSlotForTemplateModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);

		contentSlotsForTemplateRelations.add(contentSlotForTemplateModel);

		when(cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(template, hierarchyOfCatalogVersions)).thenReturn(contentSlotsForTemplateRelations);
		when(cmsPageService.getChildSlotForTemplateAtSamePosition(contentSlotsForTemplateRelations)).thenReturn(contentSlotsForTemplateRelations);

		// WHEN
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page, true);

		// THEN
		assertEquals(1, actualContentSlotsForPage.size());
		verify(contentSlotData).setIsOverrideSlot(true);
	}

	@Test
	public void willReturnPositionOfContentSlotFromPage()
	{
		// GIVEN
		final ContentSlotForPageModel contentSlotForPageModel = mock(ContentSlotForPageModel.class);
		when(contentSlotForPageModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);
		when(cmsContentSlotDao.findContentSlotRelationsByPageAndContentSlot(page, contentSlotModel, hierarchyOfCatalogVersions)).thenReturn(Collections.singletonList(contentSlotForPageModel));

		// WHEN
		String contentSlotPosition = cmsAdminContentSlotService.getContentSlotPosition(page, contentSlotModel);

		// THEN
		verify(cmsContentSlotDao, never()).findContentSlotRelationsByPageTemplateAndCatalogVersionsAndContentSlot(template, contentSlotModel, hierarchyOfCatalogVersions);
		assertThat(contentSlotPosition, equalTo(CONTENT_SLOT_POSITION));
	}

	@Test
	public void willReturnPositionOfContentSlotFromPageTemplate()
	{
		// GIVEN
		final ContentSlotForTemplateModel contentSlotForTemplateModel = mock(ContentSlotForTemplateModel.class);
		when(contentSlotForTemplateModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);
		when(cmsContentSlotDao.findContentSlotRelationsByPageAndContentSlot(page, contentSlotModel, catalogVersion)).thenReturn(Collections.emptyList());
		when(cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersionsAndContentSlot(template, contentSlotModel, hierarchyOfCatalogVersions)).thenReturn(Collections.singletonList(contentSlotForTemplateModel));

		// WHEN
		String contentSlotPosition = cmsAdminContentSlotService.getContentSlotPosition(page, contentSlotModel);

		// THEN
		assertThat(contentSlotPosition, equalTo(CONTENT_SLOT_POSITION));
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhenAddComponentToSlotAndIfSlotDoesNotHaveChangePermission()
	{
		// GIVEN
		when(permissionCRUDService.canChangeType(contentSlotModel.getItemtype())).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(cmsAdminContentSlotService).throwTypePermissionException(
				PermissionsConstants.CHANGE, contentSlotModel.getItemtype());

		// WHEN
		cmsAdminContentSlotService.addCMSComponentToContentSlot(componentModel, contentSlotModel, 1);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhenUpdateComponentPositionInSlotAndIfSlotDoesNotHaveChangePermission()
	{
		// GIVEN
		when(permissionCRUDService.canChangeType(contentSlotModel.getItemtype())).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(cmsAdminContentSlotService).throwTypePermissionException(
				PermissionsConstants.CHANGE, contentSlotModel.getItemtype());

		// WHEN
		cmsAdminContentSlotService.updatePositionCMSComponentInContentSlot(componentModel, contentSlotModel, 1);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldThrowUnknownIdentifierExceptionWhenOneOfTheRequestedContentSlotsCouldNotBeFound()
	{
		// GIVEN
		final List<String> requestedIds = Arrays.asList(SLOT_1_ID, SLOT_2_ID, SLOT_3_ID);
		final List<ContentSlotModel> slotsToReturn = Arrays.asList(slotModel1, slotModel3);

		when(cmsContentSlotDao.findContentSlotsByIdAndCatalogVersions(eq(requestedIds), any())).thenReturn(slotsToReturn);

		// WHEN / THEN
		cmsAdminContentSlotService.getContentSlots(requestedIds);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void shouldThrowAmbiguousIdentifierExceptionWhenOneOfTheRequestedContentSlotsReturnedMoreThanOneResult()
	{
		// GIVEN
		final List<String> requestedIds = Arrays.asList(SLOT_1_ID, SLOT_2_ID);
		final List<ContentSlotModel> slotsToReturn = Arrays.asList(slotModel1, slotModel2, slotModel3);

		when(slotModel3.getUid()).thenReturn(SLOT_1_ID);
		when(cmsContentSlotDao.findContentSlotsByIdAndCatalogVersions(eq(requestedIds), any())).thenReturn(slotsToReturn);

		// WHEN / THEN
		cmsAdminContentSlotService.getContentSlots(requestedIds);
	}

	@Test
	public void shouldReturnTheListOfContentSlotsRetrieved()
	{
		// GIVEN
		final List<String> requestedIds = Arrays.asList(SLOT_1_ID, SLOT_2_ID, SLOT_3_ID);
		final List<ContentSlotModel> slotsToReturn = Arrays.asList(slotModel1, slotModel2, slotModel3);

		when(cmsContentSlotDao.findContentSlotsByIdAndCatalogVersions(eq(requestedIds), any())).thenReturn(slotsToReturn);

		// WHEN
		final List<ContentSlotModel> result = cmsAdminContentSlotService.getContentSlots(requestedIds);

		// THEN
		assertThat(result, is(slotsToReturn));
	}

}
