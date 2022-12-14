/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search.validation

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.search.ItemSearchRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class NotNullSearchRequestValidatorUnitTest extends JUnitPlatformSpecification {
    def validator = new NotNullSearchRequestValidator()

    @Test
    def 'Rejects null request'() {
        when:
        validator.validate(null)

        then:
        thrown NullItemSearchRequestException
    }

    @Test
    def 'Non-null request passes validation'() {
        given:
        def request = Stub ItemSearchRequest

        when:
        validator.validate(request)

        then:
        notThrown Throwable
    }
}
