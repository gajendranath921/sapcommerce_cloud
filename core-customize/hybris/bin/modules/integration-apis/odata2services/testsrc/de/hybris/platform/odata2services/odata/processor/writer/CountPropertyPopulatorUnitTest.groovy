/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor.writer

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.InlineCount
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class CountPropertyPopulatorUnitTest extends JUnitPlatformSpecification {

	def NEXT_LINK = "next.link"
	def TOTAL_COUNT = 45
	def populator = new CountPropertyPopulator()

	@Test
	@Unroll
	def "isApplicable when ItemLookupRequest.isCount() is #isCountValue"() {

		def request = Stub(ItemLookupRequest) {
			includeTotalCount() >> isCountValue
		}

		expect:
		isApplicable == populator.isApplicable(request)

		where:
		isCountValue | isApplicable
		true         | true
		false        | false
	}

	@Test
	def "isApplicable is false when ItemLookupRequest is null"() {
		expect:
		! populator.isApplicable(null)
	}

	@Test
	def "populate provides builder with inline count"() {

		def result = Stub(ItemLookupResult) {
			getTotalCount() >> TOTAL_COUNT
		}

		when:
		def response = populator.populate(Stub(EntityProviderWriteProperties), Stub(ItemLookupRequest), result)

		then:
		response.build().inlineCount == TOTAL_COUNT
		response.build().inlineCountType == InlineCount.ALLPAGES
	}

	@Test
	def "populate provides builder with existing properties provided"() {

		def properties = new EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder()
                .nextLink(NEXT_LINK)
                .build()

		when:
		def response = populator.populate(properties, Stub(ItemLookupRequest), Stub(ItemLookupResult))

		then:
		response.build().nextLink == NEXT_LINK
	}
}
