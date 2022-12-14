/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate {@link ComponentTypeAttributeData#setI18nKey(String)} using the following
 * nomenclature:</br>
 * <tt>[prefix].[item type].[qualifier].[suffix]</tt>
 * <p>
 * The I18N key nomenclature is made up of the following chunks:</br>
 * <ul>
 * <li>prefix: a string provided via spring injection to help</li>
 * <li>suffix: a string provided via spring injection to help</li>
 * <li>item type: the platform type code that the given attribute belongs to</li>
 * <li>qualifier: the platform attribute qualifier of the given attribute</li>
 * </ul>
 * </p>
 */
public class I18nComponentTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	private static final String DOT = ".";
	private String prefix;
	private String suffix;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException
	{
		final ComposedTypeModel composedTypeModel = Objects.isNull(source.getDeclaringEnclosingType()) ? source.getEnclosingType()
				: source.getDeclaringEnclosingType();

		final String i18nKey = getPrefix() + DOT + composedTypeModel.getCode() + DOT + source.getQualifier() + DOT
				+ getSuffix();
		target.setI18nKey(i18nKey.toLowerCase());
	}

	protected String getPrefix()
	{
		return prefix;
	}

	@Required
	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

	protected String getSuffix()
	{
		return suffix;
	}

	@Required
	public void setSuffix(final String suffix)
	{
		this.suffix = suffix;
	}

}
