/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.eventtracking.services.converters;

import de.hybris.eventtracking.services.exceptions.EventTrackingInternalException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;


/**
 * @author stevo.slavic
 *
 */
public abstract class AbstractDynamicConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET>, Populator<SOURCE, TARGET>
{

	private static final Logger LOG = Logger.getLogger(AbstractDynamicConverter.class);
	private final TypeResolver<SOURCE, TARGET> typeResolver;

	protected AbstractDynamicConverter(final TypeResolver<SOURCE, TARGET> typeResolver)
	{
		this.typeResolver = typeResolver;
	}

	@Override
	public TARGET convert(final SOURCE source) throws ConversionException
	{
		final TARGET target = createTargetFromSource(source);
		if (target != null)
		{
			populate(source, target);
		}
		return target;
	}

	@Override
	public TARGET convert(final SOURCE source, final TARGET prototype) throws ConversionException
	{
		LOG.warn("Do not call this method - only call the single argument method  #convert(Object)");
		return convert(source);
	}

	protected TARGET createTargetFromSource(final SOURCE source)
	{
		final Class<? extends TARGET> targetClass = typeResolver.resolveType(source);

		return createTarget(targetClass);
	}

	protected TARGET createTarget(final Class<? extends TARGET> targetClass)
	{
		try
		{
			return targetClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new EventTrackingInternalException("Unexpected error occurred.",e);
		}
	}
}
