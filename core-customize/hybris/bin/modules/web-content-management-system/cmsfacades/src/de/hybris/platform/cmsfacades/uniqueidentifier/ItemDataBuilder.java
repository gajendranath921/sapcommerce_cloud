/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.uniqueidentifier;

import de.hybris.platform.cmsfacades.data.ItemData;

/**
 * Builder class for {@link ItemData}
 */
public class ItemDataBuilder
{
	private String itemId;
	private String name;
	private String itemType;

	private ItemDataBuilder()
	{
	}

	public ItemData build()
	{
		final ItemData itemData = new ItemData();
		itemData.setItemId(this.itemId);
		itemData.setName(this.name);
		itemData.setItemType(this.itemType);
		return itemData;
	}

	public static ItemDataBuilder newItemDataBuilder()
	{
		return new ItemDataBuilder();
	}


	public ItemDataBuilder itemId(String itemId)
	{
		this.itemId = itemId;
		return this;
	}

	public ItemDataBuilder name(String name)
	{
		this.name = name;
		return this;
	}

	public ItemDataBuilder itemType(String itemType)
	{
		this.itemType = itemType;
		return this;
	}
}
