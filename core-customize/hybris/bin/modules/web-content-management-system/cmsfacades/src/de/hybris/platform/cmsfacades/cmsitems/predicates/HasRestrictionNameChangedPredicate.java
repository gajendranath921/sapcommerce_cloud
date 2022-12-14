/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if the name has been changed by comparing the name in {@code AbstractRestrictionData} to the name in
 * {@AbstractRestrictionModel}.
 * <p>
 * Returns <tt>TRUE</tt> if the restriction name has been modified; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class HasRestrictionNameChangedPredicate implements Predicate<AbstractRestrictionModel>
{
	private OriginalClonedItemProvider originalClonedItemProvider;

	@Override
	public boolean test(final AbstractRestrictionModel abstractRestriction)
	{
		final AbstractRestrictionModel originalAbstractRestriction = (AbstractRestrictionModel) getOriginalClonedItemProvider()
				.getCurrentItem();
		return  !abstractRestriction.getName().equals(originalAbstractRestriction.getName());
	}

	protected OriginalClonedItemProvider getOriginalClonedItemProvider()
	{
		return originalClonedItemProvider;
	}

	@Required
	public void setOriginalClonedItemProvider(final OriginalClonedItemProvider originalClonedItemProvider)
	{
		this.originalClonedItemProvider = originalClonedItemProvider;
	}
}
