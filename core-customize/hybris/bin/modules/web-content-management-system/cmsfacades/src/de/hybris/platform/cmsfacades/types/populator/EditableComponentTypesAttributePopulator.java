/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator that populates the editable field of the {@link ComponentTypeAttributeData} POJO. If the source attribute
 * is not part of the provided editableAttributes set, then editable is set to false. True otherwise.
 */
public class EditableComponentTypesAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	private Set<String> editableAttributes;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target)
	{
		if (CollectionUtils.isEmpty(getEditableAttributes()) || !getEditableAttributes().contains(source.getQualifier()))
		{
			target.setEditable(false);
		}

	}

	protected Set<String> getEditableAttributes()
	{
		return editableAttributes;
	}

	@Required
	public void setEditableAttributes(final Set<String> editableAttributes)
	{
		this.editableAttributes = editableAttributes;
	}



}
