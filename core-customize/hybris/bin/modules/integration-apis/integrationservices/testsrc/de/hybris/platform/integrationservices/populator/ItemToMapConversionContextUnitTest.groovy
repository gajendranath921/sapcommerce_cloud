/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.KeyDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class ItemToMapConversionContextUnitTest extends JUnitPlatformSpecification {
    static final ITEM_TYPE = 'TestItem'

    @Test
    def "constructor fails when item type does not match the integration object item type"() {
        given: "item is not an instance of the provided type descriptor"
        def item = itemModel()
        def descriptor = Stub(TypeDescriptor) {
            isInstance(item) >> false
        }

        when:
        new ItemToMapConversionContext(item, descriptor)

        then:
        thrown(IllegalArgumentException)
    }

    @Test
    @Unroll
    def "constructor fails when #parameter is null"() {
        when:
        new ItemToMapConversionContext(item, descriptor)

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains parameter

        where:
        parameter        | item        | descriptor
        'ItemModel'      | null        | typeDescriptor()
        'TypeDescriptor' | itemModel() | null
    }

    @Test
    def "fails to create sub-context when item type does not match the integration object item type"() {
        given: "a parent context"
        def context = new ItemToMapConversionContext(itemModel(), typeDescriptor())
        and: "item that is not instance of the type descriptor"
        def item = itemModel()
        def descriptor = Stub(TypeDescriptor) {
            isInstance(item) >> false
        }

        when:
        context.createSubContext item, descriptor

        then:
        thrown(IllegalArgumentException)
    }

    @Test
    def "can add null conversion result"() {
        given:
        def context = new ItemToMapConversionContext(itemModel(), typeDescriptor())

        when:
        context.setConversionResult null

        then:
        notThrown(RuntimeException)
        context.conversionResult == null
    }

    @Test
    def "conversion result is null before a result was added"() {
        given:
        def context = new ItemToMapConversionContext(itemModel(), typeDescriptor())

        expect:
        context.conversionResult == null
    }

    @Test
    def "conversion result keeps only key attributes of the converted item"() {
        given:
        def context = new ItemToMapConversionContext(
                itemModel('Product'), typeDescriptor('Product', ['code', 'catalogVersion']))

        when:
        context.setConversionResult([
                code          : 123,
                name          : 'product',
                catalogVersion: [version: 'online', catalog: [id: 'default']],
                unit          : 'pieces'])

        then:
        context.conversionResult == [code: 123, catalogVersion: [version: 'online', catalog: [id: 'default']]]
    }

    @Test
    def "retrieves conversion result matching an item in parent context"() {
        given:
        def grandparent = new ItemToMapConversionContext(
                itemModel('Grandparent'), typeDescriptor('Grandparent'))
        grandparent.setConversionResult([key: 'grandpa', age: 60])
        def parent = grandparent.createSubContext(itemModel('Parent'), typeDescriptor('Parent', ['key']))
        parent.setConversionResult([key: 'dad', age: 35])
        def self = parent.createSubContext(parent.itemModel, parent.typeDescriptor)

        expect:
        self.conversionResult == [key: 'dad']
    }

    @Test
    def "retrieves conversion result matching an item in grandparent context"() {
        given:
        def grandparent = new ItemToMapConversionContext(
                itemModel('Grandparent'), typeDescriptor('Grandparent', ['level']))
        grandparent.setConversionResult([age: 60, level: -2])
        def parent = grandparent.createSubContext(itemModel('Parent'), typeDescriptor('Parent'))
        parent.setConversionResult([age: 35, level: -1])
        def self = parent.createSubContext grandparent.itemModel, grandparent.typeDescriptor

        expect:
        self.conversionResult == [level: -2]
    }

    ItemModel itemModel(String type = ITEM_TYPE) {
        Stub(ItemModel) {
            getItemtype() >> type
        }
    }

    IntegrationObjectItemModel integrationItemModel(def code = ITEM_TYPE, def type = ITEM_TYPE, def keys = []) {
        Stub(IntegrationObjectItemModel) {
            getCode() >> code
            getType() >> Stub(ComposedTypeModel) {
                getCode() >> type
            }
            getKeyAttributes() >> keys.collect {
                Stub(IntegrationObjectItemAttributeModel) {
                    getAttributeName() >> it
                }
            }
        }
    }

    TypeDescriptor typeDescriptor(def code = ITEM_TYPE, def keys = []) {
        Stub(TypeDescriptor) {
            getTypeCode() >> code
            getKeyDescriptor() >> Stub(KeyDescriptor) {
                isKeyAttribute(_ as String) >> { List args -> keys.contains(args[0]) }
            }
            isInstance(_) >> { true }
        }
    }

}
