/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

import java.util.function.Predicate;


/**
 * Predicate to test if a given cms item is a deleted page or not.
 * <p>
 * Returns <tt>TRUE</tt> if the item is not type AbstractPageModel or the page is not deleted; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class IsNotDeletedPagePredicate implements Predicate<CMSItemModel>
{

	@Override
	public boolean test(final CMSItemModel itemModel)
	{
		return !AbstractPageModel.class.isAssignableFrom(itemModel.getClass()) //
				|| !((AbstractPageModel) itemModel).getPageStatus().equals(CmsPageStatus.DELETED);
	}

}
