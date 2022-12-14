/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order;

/**
 * Status values for the CommerceCartModification statusCode. As the statusCode is a string it is not limited to any
 * specific set of values. These are just the values exposed by the commerceservices.
 */
public interface CommerceCartModificationStatus
{
	/**
	 * Indicates a successful modification of the cart
	 */
	String SUCCESS = "success";

	/**
	 * Indicates a failure to add the requested number of items to cart due to low stock.
	 */
	String LOW_STOCK = "lowStock";

	/**
	 * Indicates a failure to add the requested number of items to cart due to no stock.
	 */
	String NO_STOCK = "noStock";

	/**
	 * Indicates a failure to add the requested number of items to cart due to max order quantity exceeded.
	 */
	String MAX_ORDER_QUANTITY_EXCEEDED = "maxOrderQuantityExceeded";

	/**
	 * Indicates a failure to add the requested number of items to cart due to no stock in point of service.
	 */
	String MOVED_FROM_POS_TO_STORE = "movedFromPOSToStore";

	/**
	 * Indicates a failure to add the requested number of items to cart due to product unavailability (approval, online
	 * dates etc).
	 */
	String UNAVAILABLE = "unavailable";

	/**
	 * Indicates a problem with product configuration (i.e. incorrect text field value).
	 */
	String CONFIGURATION_ERROR = "configurationError";

	/**
	 * Indicates a problem with entryGroup configuration (i.e. not enough products).
	 */
	String ENTRY_GROUP_ERROR = "entryGroupError";

	/**
	 * Indicates a failure to remove the requested entry group from a cart due to no entry group with such number
	 * in the cart.
	 */
	String INVALID_ENTRY_GROUP_NUMBER = "invalidEntryGroupNumber";

	/**
	 * Indicates a successful removing the requested entry group from a cart.
	 */
	String SUCCESSFULLY_REMOVED = "succesfullyRemoved";

	/**
	 * Indicates a failure to add items to cart when numerous bundle products are added, reaching the max bundle selection limit.
	 */
	String MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED = "maxBundleSelectionCriteriaLimitExceeded";
}
