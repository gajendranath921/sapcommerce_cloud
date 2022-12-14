/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsservices.attribute;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.xyformsservices.helpers.YFormDefinitionHelper;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Supports return all the {@link YFormDefinitionModel}s from a Product Category hierarchy
 */
public class ProductAllYFormDefinitions implements DynamicAttributeHandler<List<YFormDefinitionModel>, ProductModel>, Serializable
{
	private transient YFormDefinitionHelper yFormDefinitionHelper;

	/**
	 * Returns all the {@link YFormDefinitionModel}s that are assigned to its supercategories and all their
	 * supercategories
	 */
	@Override
	public List<YFormDefinitionModel> get(final ProductModel product)
	{
		final Collection<CategoryModel> supercategories = product.getSupercategories();

		if (CollectionUtils.isNotEmpty(supercategories))
		{
			return getYFormDefinitionHelper().getAllYFormDefinitions(supercategories);
		}

		return null;
	}

	@Override
	public void set(final ProductModel product, final List<YFormDefinitionModel> value)
	{
		throw new UnsupportedOperationException();
	}

	protected YFormDefinitionHelper getYFormDefinitionHelper()
	{
		return yFormDefinitionHelper;
	}

	@Required
	public void setYFormDefinitionHelper(final YFormDefinitionHelper yFormDefinitionHelper)
	{
		this.yFormDefinitionHelper = yFormDefinitionHelper;
	}

}
