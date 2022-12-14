/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemvisitors;

import static com.google.common.collect.Lists.newLinkedList;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_TARGET_CATALOG_VERSION;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.visitor.ItemVisitor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class for visiting {@link AbstractPageModel} models for the cms synchronization service to work properly.
 * In this implementation, it is responsible for collecting all content slots and the page's restrictions.
 *
 * @param <PAGETYPE> the page type that extends {@link AbstractPageModel}
 */
public abstract class AbstractPageModelVisitor<PAGETYPE extends AbstractPageModel> implements ItemVisitor<PAGETYPE>
{
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	private SynchronizationCacheService synchronizationCacheService;

	@Override
	public List<ItemModel> visit(final PAGETYPE source, final List<ItemModel> path, final Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = newLinkedList();

		final CatalogVersionModel targetCatalogVersionModel = (CatalogVersionModel) ctx.get(VISITORS_CTX_TARGET_CATALOG_VERSION);

		final List<ContentSlotForPageModel> sourceContentSlotForPageRelations =
				getCmsSessionSearchRestrictionsDisabler().execute( //
						() -> getSynchronizationCacheService().getOrSetItemListCache( //
								() -> getCmsAdminContentSlotService().getContentSlotRelationsByPageId(source.getUid(), source.getCatalogVersion()), //
								"AbstractPageModelVisitor_ContentSlotRelationsByPageId", source //
						)
				);

		if (targetCatalogVersionModel != null)
		{
			/*
			 Extract content slot relations from the target catalog version. The content slots from target catalog version
			 will also be extracted by ContentSlotForPageModelVisitor. The synchronization process will identify these
			 relations and content slots as deleted in source catalog version and will remove them from target one.
			 Otherwise the relations and content slots will be synchronized if necessary.
			*/
			final List<CMSRelationModel> removedInSourcePageRelations = getCmsSessionSearchRestrictionsDisabler().execute(() ->
					getSynchronizationCacheService().getOrSetItemListCache( () -> getCmsAdminContentSlotService().getAllDeletedRelationsForPage(targetCatalogVersionModel, source), //
							"AbstractPageModelVisitor_RemovedInSourceCatalogVersionRelations", targetCatalogVersionModel, source) //
					);

			collectedItems.addAll(removedInSourcePageRelations);
		}

		collectedItems.addAll(sourceContentSlotForPageRelations);
		collectedItems.addAll(source.getRestrictions());
		return collectedItems.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected CMSAdminContentSlotService getCmsAdminContentSlotService()
	{
		return cmsAdminContentSlotService;
	}

	@Required
	public void setCmsAdminContentSlotService(final CMSAdminContentSlotService cmsAdminContentSlotService)
	{
		this.cmsAdminContentSlotService = cmsAdminContentSlotService;
	}

	protected SessionSearchRestrictionsDisabler getCmsSessionSearchRestrictionsDisabler()
	{
		return cmsSessionSearchRestrictionsDisabler;
	}

	@Required
	public void setCmsSessionSearchRestrictionsDisabler(
			final SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler)
	{
		this.cmsSessionSearchRestrictionsDisabler = cmsSessionSearchRestrictionsDisabler;
	}

	public SynchronizationCacheService getSynchronizationCacheService()
	{
		return synchronizationCacheService;
	}

	@Required
	public void setSynchronizationCacheService(final SynchronizationCacheService synchronizationCacheService)
	{
		this.synchronizationCacheService = synchronizationCacheService;
	}
}
