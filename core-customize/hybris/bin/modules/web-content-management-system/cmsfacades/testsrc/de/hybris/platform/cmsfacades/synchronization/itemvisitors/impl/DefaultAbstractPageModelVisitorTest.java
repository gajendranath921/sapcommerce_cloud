/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_TARGET_CATALOG_VERSION;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.model.ItemModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultAbstractPageModelVisitorTest
{

	@InjectMocks
	private DefaultAbstractPageModelVisitor visitor;
	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Mock
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	@Mock
	private ContentPageModel page;
	@Mock
	private ContentSlotForPageModel csfp1;
	@Mock
	private ContentSlotForPageModel csfp2;
	@Mock
	private ContentSlotForTemplateModel csft;
	@Mock
	private ContentSlotModel sharedSlot;
	@Mock
	private AbstractRestrictionModel restriction;
	@Mock
	private CatalogVersionModel targetCatalogVersionModel;
	@Mock
	private CatalogVersionModel sourceCatalogVersionModel;
	@Mock
	private ContentSlotForPageModel targetContentSlotForPageModel;
	@Mock
	private SynchronizationCacheService synchronizationCacheService;

	private final Map<String, Object> ctx = new HashMap<>();

	private final String PAGE_UID = "page_uid";

	@Before
	public void setUp()
	{
		ctx.put(VISITORS_CTX_TARGET_CATALOG_VERSION, targetCatalogVersionModel);
		when(page.getUid()).thenReturn(PAGE_UID);
		when(page.getCatalogVersion()).thenReturn(sourceCatalogVersionModel);
		when(page.getRestrictions()).thenReturn(asList(restriction));
		when(csft.getContentSlot()).thenReturn(sharedSlot);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(cmsSessionSearchRestrictionsDisabler).execute(any());

		doAnswer((Answer<Object>) invocation -> {
			final Supplier<List<Object>> supplier = (Supplier) invocation.getArguments()[0];
			return supplier.get();
		}).when(synchronizationCacheService).getOrSetItemListCache(any(), anyString(), any());

		doAnswer((Answer<Object>) invocation -> {
			final Supplier<List<Object>> supplier = (Supplier) invocation.getArguments()[0];
			return supplier.get();
		}).when(synchronizationCacheService).getOrSetItemListCache(any(), anyString(), any(), any());

		when(cmsAdminContentSlotService.getContentSlotRelationsByPageId(PAGE_UID, sourceCatalogVersionModel)).thenReturn(asList(csfp1, csfp2));
		when(cmsAdminContentSlotService.getAllDeletedRelationsForPage(targetCatalogVersionModel, page)).thenReturn(asList(targetContentSlotForPageModel));
	}

	@Test
	public void shouldCollectNonSharedSlotsAndRestrictionsAndRelationsFromTargetCatalogVersion()
	{
		// when
		final List<ItemModel> visit = visitor.visit(page, null, ctx);

		// then
		assertThat(visit, containsInAnyOrder(targetContentSlotForPageModel, csfp1, csfp2, restriction));
	}

}
