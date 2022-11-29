/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.eventtracking.services.converters;

import de.hybris.eventtracking.services.populators.GenericPopulator;

import java.util.List;


/**
 * @author stevo.slavic
 *
 */
public class AbstractPopulatingDynamicConverter<SOURCE, TARGET> extends AbstractDynamicConverter<SOURCE, TARGET>
{
	private List<GenericPopulator<SOURCE, TARGET>> populators;

	public AbstractPopulatingDynamicConverter(final TypeResolver<SOURCE, TARGET> typeResolver)
	{
		super(typeResolver);
	}

	public List<GenericPopulator<SOURCE, TARGET>> getPopulators()
	{
		return populators;
	}

	public void setPopulators(final List<GenericPopulator<SOURCE, TARGET>> populators)
	{
		this.populators = populators;
	}

	/**
	 * Populate the target instance from the source instance. Calls a list of injected populators to populate the
	 * instance.
	 *
	 * @param source
	 *           the source item
	 * @param target
	 *           the target item to populate
	 */
	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		final List<GenericPopulator<SOURCE, TARGET>> list = getPopulators();
		if (list != null)
		{
			for (final GenericPopulator<SOURCE, TARGET> populator : list)
			{
				if (populator != null && populator.supports(target.getClass())) // consider caching
				{
					populator.populate(source, target);
				}
			}
		}
	}
}
