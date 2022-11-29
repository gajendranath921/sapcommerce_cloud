/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
    CmsCommonsModule,
    IPageContentSlotsComponentsRestService,
    IRemoveComponentService,
    IContextAwareEditableItemService,
    IComponentVisibilityAlertService,
    IComponentSharedService,
    ISlotVisibilityService,
    IEditorEnablerService,
    IComponentMenuConditionAndCallbackService,
    SharedComponentButtonComponent,
    ExternalComponentButtonComponent,
    TypePermissionsRestService
} from 'cmscommons';
import {
    IContextualMenuButton,
    IContextualMenuConfiguration,
    IDecoratorService,
    IFeatureService,
    IPageInfoService,
    moduleUtils,
    SeEntryModule,
    SeTranslationModule,
    ISlotRestrictionsService,
    IPageService,
    ISyncPollingService,
    IEditorModalService,
    PageContentSlotsService,
    ComponentHandlerService,
    SlotSharedService,
    ISettingsService
} from 'smarteditcommons';
import {
    CmsComponentsModule,
    SlotSharedButtonComponent,
    SlotUnsharedButtonComponent,
    SlotSyncButtonComponent,
    SlotVisibilityButtonComponent
} from './components';
import {
    CmsDragAndDropService,
    ComponentEditingFacade,
    ComponentSharedService,
    ComponentVisibilityAlertService,
    EditorEnablerService,
    PageService,
    SlotRestrictionsService,
    ComponentInfoService,
    ContextAwareEditableItemService,
    ContextualMenuDropdownService,
    EditorModalService,
    HiddenComponentMenuService,
    RemoveComponentService,
    SlotContainerService,
    SlotSynchronizationService,
    SlotUnsharedService,
    SlotVisibilityService,
    SyncPollingService,
    PageContentSlotsComponentsRestService,
    OpenNodeInPageTreeService,
    ComponentMenuConditionAndCallbackService
} from './services';

@SeEntryModule('cmssmartedit')
@NgModule({
    imports: [BrowserModule, SeTranslationModule.forChild(), CmsComponentsModule, CmsCommonsModule],
    providers: [
        ContextualMenuDropdownService,
        PageContentSlotsService,
        SlotUnsharedService,
        SlotContainerService,
        HiddenComponentMenuService,
        ComponentInfoService,
        SlotSynchronizationService,
        SlotSharedService,
        {
            provide: IEditorEnablerService,
            useClass: EditorEnablerService
        },
        ComponentEditingFacade,
        CmsDragAndDropService,
        OpenNodeInPageTreeService,
        {
            provide: IPageContentSlotsComponentsRestService,
            useClass: PageContentSlotsComponentsRestService
        },
        {
            provide: ISyncPollingService,
            useClass: SyncPollingService
        },
        {
            provide: IRemoveComponentService,
            useClass: RemoveComponentService
        },
        {
            provide: IPageService,
            useClass: PageService
        },
        {
            provide: IContextAwareEditableItemService,
            useClass: ContextAwareEditableItemService
        },
        {
            provide: IEditorModalService,
            useClass: EditorModalService
        },
        {
            provide: IComponentVisibilityAlertService,
            useClass: ComponentVisibilityAlertService
        },
        {
            provide: IComponentSharedService,
            useClass: ComponentSharedService
        },
        {
            provide: ISlotRestrictionsService,
            useClass: SlotRestrictionsService
        },
        {
            provide: ISlotVisibilityService,
            useClass: SlotVisibilityService
        },
        {
            provide: IComponentMenuConditionAndCallbackService,
            useClass: ComponentMenuConditionAndCallbackService
        },
        moduleUtils.bootstrap(
            async (
                contextualMenuDropdownService: ContextualMenuDropdownService,
                editorEnablerService: IEditorEnablerService,
                decoratorService: IDecoratorService,
                featureService: IFeatureService,
                componentHandlerService: ComponentHandlerService,
                slotRestrictionsService: ISlotRestrictionsService,
                pageInfoService: IPageInfoService,
                typePermissionsRestService: TypePermissionsRestService,
                cmsDragAndDropService: CmsDragAndDropService,
                slotSharedService: SlotSharedService,
                settingsService: ISettingsService,
                openNodeInPageTreeService: OpenNodeInPageTreeService,
                componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService
            ) => {
                contextualMenuDropdownService.registerIsOpenEvent();

                editorEnablerService.enableForComponents(['^((?!Slot).)*$']);

                decoratorService.addMappings({
                    '^((?!Slot).)*$': ['se.contextualMenu', 'externalComponentDecorator'],
                    '^.*Slot$': [
                        'se.slotContextualMenu',
                        'se.basicSlotContextualMenu',
                        'syncIndicator',
                        'sharedSlotDisabledDecorator',
                        'externalSlotDisabledDecorator'
                    ]
                });

                featureService.addContextualMenuButton({
                    key: 'externalcomponentbutton',
                    priority: 100,
                    nameI18nKey: 'se.cms.contextmenu.title.externalcomponent',
                    i18nKey: 'se.cms.contextmenu.title.externalcomponentbutton',
                    regexpKeys: ['^((?!Slot).)*$'],
                    condition: componentMenuConditionAndCallbackService.externalCondition,
                    action: {
                        component: ExternalComponentButtonComponent
                    },
                    displayClass: 'externalcomponentbutton',
                    displayIconClass: 'sap-icon--globe',
                    displaySmallIconClass: 'sap-icon--globe'
                } as IContextualMenuButton);

                featureService.addContextualMenuButton({
                    key: 'se.cms.dragandropbutton',
                    priority: 200,
                    nameI18nKey: 'se.cms.contextmenu.title.dragndrop',
                    i18nKey: 'se.cms.contextmenu.title.dragndrop',
                    regexpKeys: ['^((?!Slot).)*$'],
                    condition: async (configuration: IContextualMenuConfiguration) => {
                        const slotId = componentHandlerService.getParentSlotForComponent(
                            configuration.element
                        );
                        const slotEditable = await slotRestrictionsService.isSlotEditable(slotId);
                        if (slotEditable) {
                            const hasUpdatePermission = await typePermissionsRestService.hasUpdatePermissionForTypes(
                                [configuration.componentType]
                            );
                            return hasUpdatePermission[configuration.componentType];
                        }
                        return false;
                    },
                    action: {
                        callbacks: {
                            mousedown: (): void => {
                                cmsDragAndDropService.update();
                            }
                        }
                    },
                    displayClass: 'movebutton',
                    displayIconClass: 'sap-icon--move',
                    displaySmallIconClass: 'sap-icon--move',
                    permissions: ['se.context.menu.drag.and.drop.component']
                });

                featureService.register({
                    key: 'se.cms.html5DragAndDrop',
                    nameI18nKey: 'se.cms.dragAndDrop.name',
                    descriptionI18nKey: 'se.cms.dragAndDrop.description',
                    enablingCallback: () => {
                        cmsDragAndDropService.register();
                        cmsDragAndDropService.apply();
                    },
                    disablingCallback: () => {
                        cmsDragAndDropService.unregister();
                    }
                });

                featureService.addContextualMenuButton({
                    key: 'se.cms.sharedcomponentbutton',
                    priority: 300,
                    nameI18nKey: 'se.cms.contextmenu.title.shared.component',
                    i18nKey: 'se.cms.contextmenu.title.shared.component',
                    regexpKeys: ['^((?!Slot).)*$'],
                    condition: componentMenuConditionAndCallbackService.sharedCondition,
                    action: {
                        component: SharedComponentButtonComponent
                    },
                    displayClass: 'shared-component-button',
                    displayIconClass: 'sap-icon--chain-link',
                    displaySmallIconClass: 'sap-icon--chain-link',
                    permissions: []
                });

                featureService.addContextualMenuButton({
                    key: 'se.cms.remove',
                    priority: 500,
                    customCss: 'se-contextual-more-menu__item--delete',
                    i18nKey: 'se.cms.contextmenu.title.remove',
                    nameI18nKey: 'se.cms.contextmenu.title.remove',
                    regexpKeys: ['^((?!Slot).)*$'],
                    condition: componentMenuConditionAndCallbackService.removeCondition,
                    action: {
                        callback: componentMenuConditionAndCallbackService.removeCallback
                    },
                    displayClass: 'removebutton',
                    displayIconClass: 'sap-icon--decline',
                    displaySmallIconClass: 'sap-icon--decline',
                    permissions: ['se.context.menu.remove.component']
                });

                featureService.addContextualMenuButton({
                    key: 'se.slotContextualMenuVisibility',
                    nameI18nKey: 'slotcontextmenu.title.visibility',
                    regexpKeys: ['^.*ContentSlot$'],
                    action: { component: SlotVisibilityButtonComponent },
                    permissions: ['se.slot.context.menu.visibility']
                });

                featureService.addContextualMenuButton({
                    key: 'se.slotSharedButton',
                    nameI18nKey: 'slotcontextmenu.title.shared.button',
                    regexpKeys: ['^.*Slot$'],
                    action: {
                        component: SlotSharedButtonComponent
                    },
                    permissions: ['se.slot.context.menu.shared.icon']
                });

                featureService.addContextualMenuButton({
                    key: 'slotUnsharedButton',
                    nameI18nKey: 'slotcontextmenu.title.unshared.button',
                    regexpKeys: ['^.*Slot$'],
                    action: { component: SlotUnsharedButtonComponent },
                    permissions: ['se.slot.context.menu.unshared.icon']
                });

                featureService.addContextualMenuButton({
                    key: 'se.slotSyncButton',
                    nameI18nKey: 'slotcontextmenu.title.sync.button',
                    regexpKeys: ['^.*Slot$'],
                    action: { component: SlotSyncButtonComponent },
                    permissions: ['se.sync.slot.context.menu']
                });

                featureService.addDecorator({
                    key: 'syncIndicator',
                    nameI18nKey: 'syncIndicator',
                    permissions: ['se.sync.slot.indicator']
                });

                featureService.register({
                    key: 'disableSharedSlotEditing',
                    nameI18nKey: 'se.cms.disableSharedSlotEditing',
                    descriptionI18nKey: 'se.cms.disableSharedSlotEditing.description',
                    enablingCallback: () => {
                        slotSharedService.setSharedSlotEnablementStatus(true);
                    },
                    disablingCallback: () => {
                        slotSharedService.setSharedSlotEnablementStatus(false);
                    }
                });

                featureService.addDecorator({
                    key: 'sharedSlotDisabledDecorator',
                    nameI18nKey: 'se.cms.shared.slot.disabled.decorator',
                    // only show that the slot is shared if it is not already external
                    displayCondition: async (componentType: string, componentId: string) => {
                        const [
                            isSlotEditable,
                            isExternalComponent,
                            isSlotShared
                        ] = await Promise.all([
                            slotRestrictionsService.isSlotEditable(componentId),
                            componentHandlerService.isExternalComponent(componentId, componentType),
                            slotSharedService.isSlotShared(componentId)
                        ]);
                        return !isSlotEditable && !isExternalComponent && isSlotShared;
                    }
                });

                featureService.addDecorator({
                    key: 'externalSlotDisabledDecorator',
                    nameI18nKey: 'se.cms.external.slot.disabled.decorator',
                    displayCondition: (componentType: string, componentId: string) =>
                        Promise.resolve(slotSharedService.isGlobalSlot(componentId, componentType))
                });

                featureService.addDecorator({
                    key: 'externalComponentDecorator',
                    nameI18nKey: 'se.cms.external.component.decorator',
                    displayCondition: (componentType: string, componentId: string) =>
                        Promise.resolve(
                            componentHandlerService.isExternalComponent(componentId, componentType)
                        )
                });

                featureService.addContextualMenuButton({
                    key: 'clonecomponentbutton',
                    priority: 600,
                    nameI18nKey: 'se.cms.contextmenu.title.clone.component',
                    i18nKey: 'se.cms.contextmenu.title.clone.component',
                    regexpKeys: ['^((?!Slot).)*$'],
                    condition: componentMenuConditionAndCallbackService.cloneCondition,
                    action: {
                        callback: componentMenuConditionAndCallbackService.cloneCallback
                    },
                    displayClass: 'clonebutton',
                    displayIconClass: 'sap-icon--duplicate',
                    displaySmallIconClass: 'sap-icon--duplicate',
                    permissions: ['se.clone.component']
                });

                const pageTreeEnabled = await settingsService.get('smartedit.pagetree.enabled');
                if (pageTreeEnabled === 'true') {
                    featureService.addContextualMenuButton({
                        key: 'se.cms.openInPageTreeButton',
                        priority: 700,
                        nameI18nKey: 'se.cms.contextmenu.title.open.in.page.tree',
                        i18nKey: 'se.cms.contextmenu.title.open.in.page.tree',
                        regexpKeys: ['^((?!Slot).)*$'],
                        action: {
                            callback: (configuration: IContextualMenuConfiguration): void => {
                                const elementUuid =
                                    configuration.componentAttributes.smarteditElementUuid;
                                openNodeInPageTreeService.publishOpenEvent(elementUuid);
                            }
                        },
                        displayIconClass: 'icon-tree',
                        displaySmallIconClass: 'icon-tree',
                        permissions: ['se.read.page']
                    });
                }
            },
            [
                ContextualMenuDropdownService,
                IEditorEnablerService,
                IDecoratorService,
                IFeatureService,
                ComponentHandlerService,
                ISlotRestrictionsService,
                IPageInfoService,
                TypePermissionsRestService,
                CmsDragAndDropService,
                SlotSharedService,
                ISettingsService,
                OpenNodeInPageTreeService,
                IComponentMenuConditionAndCallbackService
            ]
        )
    ]
})
export class CmssmarteditModule {}
