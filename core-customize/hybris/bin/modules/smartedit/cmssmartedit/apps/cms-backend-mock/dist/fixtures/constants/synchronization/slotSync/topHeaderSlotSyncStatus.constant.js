"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.topHeaderSlotSyncStatus = void 0;
const catalogVersionUuid = 'apparel-ukContentCatalog/Staged';
exports.topHeaderSlotSyncStatus = {
    catalogVersionUuid,
    itemId: 'topHeaderSlot',
    itemType: 'topHeaderSlotContentSlot',
    name: 'topHeaderSlot',
    lastSyncStatus: new Date().getTime(),
    status: 'NOT_SYNC',
    selectedDependencies: [
        {
            catalogVersionUuid,
            itemId: 'component1',
            itemType: 'ContentSlot',
            name: 'component1',
            lastSyncStatus: new Date().getTime(),
            status: 'NOT_SYNC',
            dependentItemTypesOutOfSync: [
                {
                    type: 'Navigation',
                    i18nKey: 'some.key.for.Navigation'
                },
                {
                    type: 'Customization',
                    i18nKey: 'some.key.for.Customization'
                }
            ]
        },
        {
            itemId: 'component2',
            itemType: 'SimpleBannerComponent',
            name: 'component2',
            lastSyncStatus: new Date().getTime(),
            status: 'IN_SYNC',
            catalogVersionUuid
        }
    ],
    dependentItemTypesOutOfSync: [
        {
            type: 'ContentSlot',
            i18nKey: 'some.key.for.component1'
        }
    ],
    unavailableDependencies: []
};
//# sourceMappingURL=topHeaderSlotSyncStatus.constant.js.map