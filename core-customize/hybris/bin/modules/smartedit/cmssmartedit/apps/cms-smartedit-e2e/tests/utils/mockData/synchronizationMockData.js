/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
unit.mockData.synchronization = function () {
    this.SLOT1_SYNC_STATUS = {
        itemId: 'slot1',
        name: 'slot1',
        itemType: 'slot1',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3'],
        fromSharedDependency: false
    };

    this.SLOT2_SYNC_STATUS = {
        itemId: 'slot2',
        name: 'slot2',
        itemType: 'slot2',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3', 'someItem4']
    };

    this.SLOT3_SYNC_STATUS = {
        itemId: 'slot3',
        name: 'slot3',
        itemType: 'slot3',
        status: 'SOME_STATUS',
        fromSharedDependency: true
    };

    this.PAGE_ID1_SYNC_STATUS = {
        itemId: 'pageId1',
        name: 'pageId1',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [this.SLOT1_SYNC_STATUS, this.SLOT2_SYNC_STATUS],
        sharedDependencies: [this.SLOT3_SYNC_STATUS]
    };

    this.PAGE_ID2_SYNC_STATUS = {
        itemId: 'pageId2',
        name: 'pageId2',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [this.SLOT1_SYNC_STATUS, this.SLOT2_SYNC_STATUS],
        sharedDependencies: [this.SLOT3_SYNC_STATUS]
    };
};
