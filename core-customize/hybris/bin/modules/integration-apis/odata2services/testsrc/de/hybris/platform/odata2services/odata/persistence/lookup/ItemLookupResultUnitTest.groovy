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

package de.hybris.platform.odata2services.odata.persistence.lookup

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ItemLookupResultUnitTest extends JUnitPlatformSpecification {
    @Test
    def "creates instance from flexible search result"() {
        given:
        def searchResult = Stub(SearchResult) {
            getTotalCount() >> 2
            getResult() >> ['entry one', 'entry two']
        }

        expect:
        with(ItemLookupResult.createFrom(searchResult)) {
            totalCount == searchResult.totalCount
            entries == searchResult.result
        }
    }

    @Test
    def "creates instance from items and count"() {
        given:
        def count = 30
        def items = [1, 2, 3]

        expect:
        with(ItemLookupResult.createFrom(items, count)) {
            totalCount == count
            entries == items
        }
    }

    @Test
    def "creates instance from items"() {
        given:
        def items = [Stub(ItemModel)]

        expect:
        with(ItemLookupResult.createFrom(items)) {
            totalCount == -1
            entries == items
        }
    }

    @Test
    def "entries collection is immutable"() {
        given:
        def result = ItemLookupResult.createFrom(["one", "two"])

        when:
        result.entries.clear()

        then:
        thrown UnsupportedOperationException
    }

    @Test
    def "can transform to a different type of entries"() {
        given:
        def result = ItemLookupResult.createFrom([1, 2, 3, 5, 8], 13)

        expect:
        with(result.map({String.valueOf(it)})) {
            entries == ["1", "2", "3", "5", "8"]
            totalCount == result.totalCount
        }
    }
}
