/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.integrationkey.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.KeyAttribute
import de.hybris.platform.integrationservices.model.KeyDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test

@UnitTest
class DefaultAlphabeticalIntegrationKeyMetadataGeneratorUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new IntegrationObjectItemModel()

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    def keyDescriptor = Stub KeyDescriptor
    def descriptorFactory = Stub(DescriptorFactory) {
        createItemTypeDescriptor(ITEM) >> Stub(TypeDescriptor) {
            getKeyDescriptor() >> keyDescriptor
        }
    }
    def integrationKeyGenerator = new DefaultAlphabeticalIntegrationKeyMetadataGenerator(
            descriptorFactory: descriptorFactory)

    @Test
    def 'generates correct key when descriptor factory is not injected'() {
        given: 'descriptor factory is not injected'
        integrationKeyGenerator.descriptorFactory = null
        and: 'descriptor factory is present in the application context'
        applicationContext.addBean 'integrationServicesDescriptorFactory', Stub(DescriptorFactory) {
            createItemTypeDescriptor(ITEM) >> Stub(TypeDescriptor) {
                getKeyDescriptor() >> keyDescriptor
            }
        }
        and:
        keyDescriptor.getKeyAttributes() >> [keyAttribute('MyItem', 'code')]

        expect:
        integrationKeyGenerator.generateKeyMetadata(ITEM) == "MyItem_code"
    }

    @Test
    def "generate null throws IllegalArgumentException"() {
        when:
        integrationKeyGenerator.generateKeyMetadata(null)

        then:
        thrown(IllegalArgumentException)
    }

    @Test
    def "key is empty when item has no unique attributes"() {
        given:
        keyDescriptor.getKeyAttributes() >> []

        expect:
        integrationKeyGenerator.generateKeyMetadata(ITEM).empty
    }

    @Test
    def "simple key attribute"() {
        given:
        keyDescriptor.getKeyAttributes() >> [keyAttribute('MyItem', 'code')]

        expect:
        integrationKeyGenerator.generateKeyMetadata(ITEM) == "MyItem_code"
    }

    @Test
    def "two key attributes for same type are present in the attribute name order"() {
        given:
        keyDescriptor.getKeyAttributes() >> [keyAttribute('Type', 'attr2'), keyAttribute('Type', 'attr1')]

        expect:
        integrationKeyGenerator.generateKeyMetadata(ITEM) == "Type_attr1|Type_attr2"
    }

    @Test
    def "three key attributes for different types are present in the order of the item types"() {
        given:
        keyDescriptor.getKeyAttributes() >> [keyAttribute('TypeC', 'attr1'),
                                             keyAttribute('TypeB', 'attr2'),
                                             keyAttribute('TypeA', 'attr3')]

        expect:
        integrationKeyGenerator.generateKeyMetadata(ITEM) == 'TypeA_attr3|TypeB_attr2|TypeC_attr1'
    }

    @Test
    def "duplicate unique attributes appear only once"() {
        given:
        keyDescriptor.getKeyAttributes() >> [keyAttribute('TypeA', 'attr'),
                                             keyAttribute('TypeB', 'attr'),
                                             keyAttribute('TypeA', 'attr'),
                                             keyAttribute('TypeB', 'attr')]

        expect:
        integrationKeyGenerator.generateKeyMetadata(ITEM) == 'TypeA_attr|TypeB_attr'
    }

    private KeyAttribute keyAttribute(String itemCode, String attrName) {
        Stub(KeyAttribute) {
            getItemCode() >> itemCode
            getName() >> attrName
        }
    }
}
