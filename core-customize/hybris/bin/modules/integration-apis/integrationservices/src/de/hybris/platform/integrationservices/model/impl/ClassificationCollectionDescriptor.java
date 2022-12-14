/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.integrationservices.model.CollectionDescriptor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of the {@link CollectionDescriptor} relating to classification attributes. If the underlying
 * attribute is not of <code>multivalued</code>, this implementation
 * does not consider the attribute as a collection. Therefore, the new collection will be null. If the attribute is multivalued,
 * an empty {@link java.util.List} will be returned since multivalued classification attributes are always stored in a List
 */
public class ClassificationCollectionDescriptor implements CollectionDescriptor
{
	private final boolean multivalued;

	private ClassificationCollectionDescriptor(final ClassAttributeAssignmentModel classAttributeAssignment)
	{
		multivalued = classAttributeAssignment.getMultiValued();
	}

	public static CollectionDescriptor create(final ClassAttributeAssignmentModel classAttributeAssignment)
	{
		return new ClassificationCollectionDescriptor(classAttributeAssignment);
	}

	@Override
	public Collection<Object> newCollection()
	{
		return multivalued ? new ArrayList<>() : null;
	}
}
