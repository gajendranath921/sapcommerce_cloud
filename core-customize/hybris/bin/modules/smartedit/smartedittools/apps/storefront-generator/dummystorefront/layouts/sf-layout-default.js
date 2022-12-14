/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint undef:false */
sfConfigManager.registerLayout(sfConfigManager.ALIASES.DEFAULT_LAYOUT_ALIAS, {
    pageId: 'homepage',
    catalogVersion: 'apparel-ukContentCatalog/Staged',
    nodeType: 'root',
    children: [
        {
            componentId: 'topHeaderSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [
                {
                    componentId: 'component1',
                    componentType: 'componentType1',
                    nodeType: 'component',
                    view: {
                        hasTemplate: true,
                        basePath: 'BUNDLE_ROOT_PLACEHOLDER',
                        showHeader: false
                    }
                },
                {
                    componentId: 'component2',
                    componentType: 'componentType2',
                    nodeType: 'component',
                    view: {
                        hasTemplate: true,
                        basePath: 'BUNDLE_ROOT_PLACEHOLDER'
                    }
                },
                {
                    componentId: 'component3',
                    componentType: 'SimpleResponsiveBannerComponent',
                    nodeType: 'component',
                    view: {
                        hasTemplate: true,
                        basePath: 'BUNDLE_ROOT_PLACEHOLDER'
                    }
                }
            ]
        },
        {
            alias: 'sfBuilderFixtures',
            nodeType: 'alias'
        },
        {
            componentId: 'bottomHeaderSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [
                {
                    componentId: 'component4',
                    componentType: 'componentType4',
                    nodeType: 'component',
                    catalogVersion: 'apparelContentCatalog/Online'
                }
            ].concat(
                Array.apply(null, {
                    length: 20
                }).map(function (element, index) {
                    return {
                        alias: 'component-0' + (index + 1),
                        nodeType: 'alias'
                    };
                })
            )
        },
        {
            componentId: 'footerSlot',
            componentType: 'ContentSlot',
            catalogVersion: 'apparel-ukContentCatalog/Staged',
            nodeType: 'slot',
            children: [
                {
                    componentId: 'component5',
                    componentType: 'componentType5',
                    nodeType: 'component'
                }
            ]
        },
        {
            componentId: 'searchBoxSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [
                {
                    componentId: 'component8',
                    componentType: 'componentType8',
                    nodeType: 'component'
                }
            ]
        },
        {
            componentId: 'miniCartSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [
                {
                    componentId: 'component10',
                    componentType: 'componentType10',
                    nodeType: 'component'
                },
                {
                    componentId: 'component11',
                    componentType: 'componentType11',
                    nodeType: 'component'
                }
            ]
        },
        {
            alias: 'slotWrapper',
            nodeType: 'alias'
        },
        {
            componentId: 'headerLinksSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [
                {
                    componentId: 'component9',
                    componentType: 'componentType9',
                    nodeType: 'component'
                }
            ]
        },
        {
            componentId: 'deepLinksSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            view: {
                hasTemplate: true,
                basePath: 'BUNDLE_ROOT_PLACEHOLDER',
                showHeader: false
            },
            children: []
        },
        {
            componentId: 'emptyDummySlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: []
        }
    ]
});
