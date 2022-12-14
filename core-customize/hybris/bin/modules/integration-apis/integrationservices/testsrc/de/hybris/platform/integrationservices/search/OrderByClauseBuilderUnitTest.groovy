/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.enums.RelationEndCardinalityEnum
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.RelationDescriptorModel
import de.hybris.platform.core.model.type.RelationMetaTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue

@UnitTest
class OrderByClauseBuilderUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM_TYPE = 'Product'

    def service = Stub(IntegrationObjectService)
    def builder = new FlexibleSearchQueryBuilder(service)

    @Test
    def "order by localized and non-localized attributes"() {
        given:
        def itemMetadata = itemWithAttributes([
                attribute(qualifier: 'name', localized: true),
                attribute(qualifier: 'price', localized: false)])
        and:
        def orderExpressions = [new OrderExpression("name", OrderBySorting.DESC),
                                new OrderExpression("price", OrderBySorting.ASC)]

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withOrderBy(orderExpressions)
                .withLocale(Locale.ENGLISH)
                .build().query

        then:
        searchQuery == "SELECT DISTINCT {product:pk}, {product:name[en]}, {product:price} FROM {$ITEM_TYPE* AS product} ORDER BY {product:name[en]} DESC, {product:price} ASC"
    }

    @Test
    def "combine order by and filter by on localized and non-localized attributes"() {
        given:
        def itemMetadata = itemWithAttributes([
                attribute(qualifier: 'name', localized: true),
                attribute(qualifier: 'price', localized: false)])
        and:
        def orderExpressions = [new OrderExpression("name", OrderBySorting.DESC),
                                new OrderExpression("price", OrderBySorting.ASC)]
        and:
        def condition = new SimplePropertyWhereClauseCondition('name', '=', 'some_name')
        def filter = new WhereClauseConditions(condition)

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withFilter(filter)
                .withOrderBy(orderExpressions)
                .withLocale(Locale.ENGLISH)
                .build().query

        then:
        searchQuery == "SELECT DISTINCT {product:pk}, {product:name[en]}, {product:price} FROM {Product* AS product} WHERE {product:name[en]} = 'some_name' ORDER BY {product:name[en]} DESC, {product:price} ASC"
    }

    @Test
    @Issue('https://jira.tools.sap/browse/CXEC-7485')
    def "query contains orderBy and where clause with filter when filter and orderBy are present in navigation property attributes"() {
        given:
        final def SOME_CODE = 'some_code'
        def itemMetadata = itemWithSourceRelation('supercategories', false, 'CategoryProductRelation', RelationEndCardinalityEnum.MANY, RelationEndCardinalityEnum.MANY)
        and:
        def orderExpressions = [new OrderExpression("code", OrderBySorting.DESC)]
        and:
        def condition = SimplePropertyWhereClauseCondition.eq('supercategories', SOME_CODE)
        def filter = new WhereClauseConditions(condition)

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withFilter(filter)
                .withOrderBy(orderExpressions)
                .build().query

        then:
        searchQuery == "SELECT DISTINCT {product:pk}, {product:code} FROM {Product* AS product JOIN CategoryProductRelation AS categoryproductrelation ON {product:pk} = {categoryproductrelation:target}} WHERE {categoryproductrelation:source} = '$SOME_CODE' ORDER BY {product:code} DESC"
    }

    @Test
    def "order by PK only when the order by clause is not provided [#orderBy]"() {

        given:
        def itemMetadata = item()

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .orderedByPK()
                .withOrderBy(orderBy)
                .build().query

        then:
        searchQuery == expectedResult

        where:
        orderBy | expectedResult
        null    | "SELECT DISTINCT {product:pk} FROM {$ITEM_TYPE* AS product} ORDER BY {product:pk}"
        []      | "SELECT DISTINCT {product:pk} FROM {$ITEM_TYPE* AS product} ORDER BY {product:pk}"

    }

    @Test
    def "order by clause is not added to select statement when PK ordering is not specified and orderBy #orderBy"() {

        given:
        def itemMetadata = item()

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withOrderBy(orderBy)
                .build().query

        then:
        searchQuery == expectedResult

        where:
        orderBy | expectedResult
        null    | "SELECT DISTINCT {product:pk} FROM {$ITEM_TYPE* AS product}"
        []      | "SELECT DISTINCT {product:pk} FROM {$ITEM_TYPE* AS product}"

    }

    @Test
    def "throws an OrderByVirtualAttributeNotSupportedException when order by clause includes a virtual attribute"() {
        given: 'an item with a virtual attribute'
        def virtualAttribute = virtualAttribute('virtualAttributeTest')
        def itemMetadata = itemWithVirtualAttributes([virtualAttribute])
        and: 'the attribute is used in the orderBy expression'
        def orderExpressions = [new OrderExpression(virtualAttribute.attributeName, OrderBySorting.DESC)]

        when:
        builder
                .withIntegrationObjectItem(itemMetadata)
                .withOrderBy(orderExpressions)
                .orderByClause()

        then:
        def e = thrown OrderByVirtualAttributeNotSupportedException
        e.attribute == virtualAttribute
    }

    @Test
    def "does not throw an exception when item contains a virtual attribute by order by clause does not refer it"() {
        given: 'item contains virtual and non-virtual attributes'
        def attribute = attribute(qualifier: 'standard')
        def itemMetadata = item().tap {
            it.getAttributes() >> [attribute]
            it.getVirtualAttributes() >> [virtualAttribute('virtual')]
        }
        and: 'the order expression does not contain the virtual attribute'
        def orderExpressions = [new OrderExpression(attribute.attributeName, OrderBySorting.ASC)]

        when:
        builder
                .withIntegrationObjectItem(itemMetadata)
                .withOrderBy(orderExpressions)
                .orderByClause()

        then:
        noExceptionThrown()
    }

    @Test
    def "withOrderBy does not leak reference when given orderByExpression is not null"() {
        given:
        def orderExpressions = [new OrderExpression("name", OrderBySorting.DESC)]
        def searchQuery = queryBuilderForSomeItem()
                .withOrderBy(orderExpressions)

        when:
        orderExpressions.clear()

        then: 'orderExpressions are not empty and converted to the ORDER BY clause'
        searchQuery.build().query.contains('ORDER BY')
    }

    @Test
    def "query has no ORDER BY when the orderByExpression is #msg"() {
        given:
        def searchQuery = builder
                .withOrderBy(inputValue)
                .build().query

        expect:
        !searchQuery.contains('ORDER BY')

        where:
        msg     | inputValue
        "empty" | []
        "null"  | null
    }

    private IntegrationObjectItemModel itemWithAttributes(List attributes) {
        item().tap {
            it.getAttributes() >> attributes
        }
    }

    def itemWithVirtualAttributes(List virtualAttributes) {
        item().tap {
            it.getVirtualAttributes() >> virtualAttributes
        }
    }

    def itemWithSourceRelation(String attributeName, Boolean isSource, String relationName, RelationEndCardinalityEnum sourceCardinality, RelationEndCardinalityEnum targetCardinality, sourceCode = 'Product') {
        Stub(IntegrationObjectItemModel) {
            getType() >> Stub(ComposedTypeModel) {
                getDeclaredattributedescriptors() >> [relation(attributeName, isSource, relationName, sourceCardinality, targetCardinality)]
                getCode() >> sourceCode
            }
            getItemTypeMatch() >> null
            getAttributes() >> [attribute(attributeName, attributeName)]
        }
    }

    def relation(String targetCode, Boolean isSource, String relationName, RelationEndCardinalityEnum sourceCardinality, RelationEndCardinalityEnum targetCardinality) {
        Stub(RelationDescriptorModel) {
            getRelationType() >> Stub(RelationMetaTypeModel) {
                getTargetType() >> Stub(ComposedTypeModel) {
                    getCode() >> targetCode
                }
                getSourceTypeCardinality() >> sourceCardinality
                getTargetTypeCardinality() >> targetCardinality
            }
            getRelationName() >> relationName
            getIsSource() >> isSource  // if false, target and source are switched.
        }
    }

    def item() {
        Stub(IntegrationObjectItemModel) {
            getType() >> Stub(ComposedTypeModel) {
                getCode() >> ITEM_TYPE
            }
            getItemTypeMatch() >> null
        }
    }

    IntegrationObjectItemAttributeModel attribute(Map<String, ?> params) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> params['qualifier']
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getQualifier() >> params['qualifier']
                getLocalized() >> (params['localized'] ?: Boolean.FALSE)
            }
        }
    }

    IntegrationObjectItemAttributeModel attribute(String name, String typeCode) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> name
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getPersistenceClass() >> Object.class
                getAttributeType() >> Stub(ComposedTypeModel) {
                    getCode() >> typeCode
                }
                getQualifier() >> typeCode
            }
        }
    }

    IntegrationObjectItemVirtualAttributeModel virtualAttribute(String name) {
        Stub(IntegrationObjectItemVirtualAttributeModel) {
            getAttributeName() >> name
        }
    }

    FlexibleSearchQueryBuilder queryBuilderForSomeItem() {
        builder.withIntegrationObjectItem itemWithAttributes([attribute(qualifier: 'does-not-matter')])
    }
}
