/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.uniqueidentifier;

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.ItemModel;


/**
 * Interface that defines apply methods for bidirectional conversion between
 * {@link ItemModel} and {@link ItemData}.
 * @param <T> type parameter which extends the {@link ItemModel} type
 */
public interface UniqueIdentifierConverter<T extends ItemModel>
{

	/**
	 * Returns the item type {@code ItemModel#getItemType} where the converter should be applied to.
	 *
	 * @return item type
	 */
	String getItemType();

	/**
	 * Convert an ItemModel object to the corresponding {@link ItemData} instance.
	 *
	 * @param itemModel
	 *           - the item model entity that will be converted.
	 * @return an instance of the {@link ItemData} class that corresponds to the given {@link ItemModel}
	 *         {@link UniqueIdentifierConverter#getItemType}.
	 * @see {@link UniqueIdentifierConverter#getItemType()}.
	 * @throws IllegalArgumentException when {@code itemModel} is null.
	 */
	ItemData convert(final T itemModel);

	/**
	 * Convert an {@link ItemData} object to the corresponding {@link ItemModel} entity.
	 *
	 * @param itemData
	 *           - the item data instance that will be used in the conversion.
	 * @return the {@link ItemModel} entity that corresponds to the given {@link ItemData}
	 *         {@link UniqueIdentifierConverter#getItemType}.
	 * @see {@link UniqueIdentifierConverter#getItemType()}.
	 */
	T convert(final ItemData itemData);

}
