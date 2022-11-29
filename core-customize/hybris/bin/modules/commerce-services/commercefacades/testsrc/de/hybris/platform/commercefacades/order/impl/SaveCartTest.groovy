/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commerceservices.order.CommerceSaveCartException

import spock.lang.Ignore
import org.junit.Test
import org.joda.time.DateTime;

/**
 *
 * combines tests from Save_Cart.txt plus DefaultSaveCartFacadeIntegrationTest.java
 *
 */
@IntegrationTest
class SaveCartTest extends AbstractCommerceFacadesSpockTest {

	def CUSTOMER_ID_1 = "customer1@test.com"
	def CUSTOMER_ID_2 = "customer2@test.com"

	def SAVED_CART_NAME_1 = "Test Saved Cart name 1"
	def SAVED_CART_DESCRIPTION_1 = "Test Saved Cart description 1"

	def SAVED_CART_NAME_2 = "Test Saved Cart name 2"
	def SAVED_CART_DESCRIPTION_2 = "Test Saved Cart description 2"

	def SAVED_CART_NAME_3 = "Test Saved Cart name 3"
	def SAVED_CART_DESCRIPTION_3 = "Test Saved Cart description 3"
	def EXP_DAYS = 30

	def setup() {
		importCsv("/commercefacades/test/testCommerceServices.csv", "utf-8")
	}


	@Test
	def "Get saved carts count for current customer"() {

		given: "a session with a logged in user who has x saved carts"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)
		def x = getSavedCartsForCurrentUser().size()

		when: "we save another cart for the same customer"
		def cartData = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		then: "the number of saved carts for that customer should increase to x + 1"
		getSavedCartsForCurrentUser().size() == x+1
	}

	@Test
	def "Test Save Cart AnonymousSessionCart fail"() {
		// Testing: Save Cart not possible for Anonymous Session Cart

		given: "a session with annonymous user"

		when: "we save the cart"
		saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		then: "an exception is thrown and the session cart save info is not populated"
		CommerceSaveCartException e = thrown()
		e.message == "Anonymous carts cannot be saved. The user must be logged in."
		with (getCurrentSessionCart()) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}
	}

	@Ignore
	@Test
	def "Test Save Cart CustomerSessionCart Name Empty AutoGenerated"() {
		//		Save Cart requires 2 parameters to be provided (name, description) - Testing: name = ${EMPTY}
		//		...                If any of these parameter's value is not provided, then will be automatically generated
		//		...                name: 8-digit number
		//		...                description: -

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart with an empty name"
		def cartData = saveCartWithNameAndDescription("",SAVED_CART_DESCRIPTION_1)

		then: "the cart is saved with a generated 8 digit name and result data matches session cart data"
		with(cartData) {
			name.matches("\\d{8}")
			description == SAVED_CART_DESCRIPTION_1
			saveTime != null
			expirationTime.getTime() >=  new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
			savedBy.uid == CUSTOMER_ID_1
		}
		resultCartDataMatchesSessionCartData(cartData)
	}

	@Test
	def "Test Save Cart CustomerSessionCart Description Empty AutoGenerated"() {
		// Save Cart requires 2 parameters to be provided (name, description) - Testing: description = ${EMPTY}

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart with an empty description"
		def cartData = saveCartWithNameAndDescription(SAVED_CART_NAME_1,"")

		then: "the cart is saved with a generated 8 digit name, a dash in the description, and result data matches session cart data"
		with(cartData) {
			name == SAVED_CART_NAME_1
			description == "-"
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
			savedBy.uid == CUSTOMER_ID_1
		}
		resultCartDataMatchesSessionCartData(cartData)
	}

	@Ignore
	@Test
	def "Test Save Cart CustomerSessionCart NameAndDescription Empty_AutoGenerated"() {
		// Save Cart requires 2 parameters to be provided (name, description) - Testing: name AND description = ${EMPTY}

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart with an empty description"
		def cartData = saveCartWithNameAndDescription("","")

		then: "the cart is saved with a dash in the description and result data matches session cart data"
		with(cartData) {
			name.matches("\\d{8}")
			description == "-"
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
			savedBy.uid == CUSTOMER_ID_1
		}
		resultCartDataMatchesSessionCartData(cartData)
	}

	@Test
	def "Test Save Cart CustomerSessionCart NameAndDescription Provided"() {
		//		Testing: Save Cart for Customer Session Cart
		//		...                Verification of provided name and description parameters and other: saveTime, savedBy, expirationTime

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart with  a valid name and description"
		def cartData = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		then: "the cart is saved with a dash in the description and result data matches session cart data"
		with(cartData) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
			savedBy.uid == CUSTOMER_ID_1
		}
		resultCartDataMatchesSessionCartData(cartData)
	}

	@Test
	def "Test Save Cart CustomerSessionCart CartCode CorrectProvided "() {
		//		Testing: Save Cart for Customer Session Cart
		//		...                Verification of savedCart.code that was provided as a parameter for Save Cart

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart by specifying its code"
		def sessionCart = getCurrentSessionCart()
		def cartData = saveGivenCartWithNameAndDescription(sessionCart.code, SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		then: "the cart code of the saved cart is the same as the code of the session cart"
		cartData.code == sessionCart.code
	}


	@Test
	def "Test Save Cart CustomerSessionCart CartCode IncorrectProvided Exception"() {
		//		Testing: Save Cart for Customer Session Cart
		//		...                Verification expected exception due to incorrect(non-existing) cart code provided as a parameter when saving cart

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart by specifying an incorrect code"
		def cartData = saveGivenCartWithNameAndDescription("incorrectCartCode", SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		then: "exception is thrown"
		CommerceSaveCartException e = thrown()
		e.message == "Cannot find a cart for code [incorrectCartCode]"
	}

	@Test
	def "Test Save Cart CustomerSessionCart CartCode NotProvided"() {
		// Testing: Save Cart for Customer Session Cart
		//	...                Verification of savedCart.code that if was nt provided for Save Cart, is automatically provided as current cart session code

		given: "a session with a logged in customer and the session cart"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the cart"
		def cartData = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		then: "the cart code of the saved cart is the same as the code of the session cart"
		cartData.code == sessionCart.code
	}

	@Test
	def "Test Save Cart CustomerSessionCart MultipleCustomers"() {
		//		Testing: Save Cart for multiple customers
		//		...                Verification of parameters for each Session Cart

		given: "two sessions with different logged in users"
		createCustomer(CUSTOMER_ID_1)
		createCustomer(CUSTOMER_ID_2)


		when: "we log in each user in turn and save their session cart"
		login(CUSTOMER_ID_1)
		def savedCart1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		removeAndCreateNewSessionCart()

		login(CUSTOMER_ID_2)
		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		then: "both saved carts have the correct data"
		with(savedCart1) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}

		with(savedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_2
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
	}

	@Test
	def "Test Save Cart CustomerSessionCart NewSessionCart_No1"() {
		//		Testing: Save Cart for newly created Session Cart
		//		...                Verification of parameters for old and new Session Cart

		given: "a test customer logged into a session"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save two different sessions carts for the same customer"
		def savedCart1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		removeAndCreateNewSessionCart()

		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		then: "both saved carts have the correct data"
		with(savedCart1) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
		with(savedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
	}

	@Ignore
	@Test
	def "Test Save Cart CustomerSessionCart NewSessionCart_No2"() {
		//		Testing: Save Cart for newly created Session Cart - 3 new Session Carts created in a row
		//		...                Verification of parameters for old and new Session Cart

		given: "a test customer logged into a session"
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save four different sessions carts for the same customer"
		def savedCart1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		removeAndCreateNewSessionCart()

		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		removeAndCreateNewSessionCart()

		def savedCart3 = saveCartWithNameAndDescription(SAVED_CART_NAME_3,SAVED_CART_DESCRIPTION_3)

		removeAndCreateNewSessionCart()

		def savedCart4 = saveCartWithNameAndDescription("","")

		then: "all four saved carts have the correct data"
		with(savedCart1) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
		with(savedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
		with(savedCart3) {
			name == SAVED_CART_NAME_3
			description == SAVED_CART_DESCRIPTION_3
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
		with(savedCart4) {
			name.matches("\\d{8}")
			description == "-"
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime.getTime() >= new DateTime(saveTime).plusDays(EXP_DAYS).toDate().getTime()
		}
	}
}