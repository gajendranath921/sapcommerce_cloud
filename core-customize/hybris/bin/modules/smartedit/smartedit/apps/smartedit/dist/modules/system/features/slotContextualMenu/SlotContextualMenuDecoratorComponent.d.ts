/// <reference types="angular" />
/// <reference types="jquery" />
import { DoCheck, ElementRef, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ComponentAttributes, IContextualMenuButton, IContextualMenuService, NodeUtils, SystemEventService, CrossFrameEventService } from 'smarteditcommons';
import { BaseContextualMenuComponent } from '../contextualMenu/BaseContextualMenuComponent';
export declare class SlotContextualMenuDecoratorComponent extends BaseContextualMenuComponent implements OnInit, OnDestroy, OnChanges, DoCheck {
    private readonly element;
    private readonly yjQuery;
    private readonly systemEventService;
    private readonly contextualMenuService;
    private readonly nodeUtils;
    private readonly crossFrameEventService;
    smarteditComponentType: string;
    smarteditComponentId: string;
    smarteditContainerType: string;
    smarteditContainerId: string;
    smarteditSlotId: string;
    smarteditSlotUuid: string;
    smarteditCatalogVersionUuid: string;
    smarteditElementUuid: string;
    componentAttributes: ComponentAttributes;
    set active(_active: string | boolean);
    get active(): string | boolean;
    items: IContextualMenuButton[];
    itemsrc: string;
    showAtBottom: boolean;
    private oldRightMostOffsetFromPage;
    private readonly maxContextualMenuItems;
    private showSlotMenuUnregFn;
    private hideSlotMenuUnregFn;
    private refreshContextualMenuUnregFn;
    private readonly hideSlotUnSubscribeFn;
    private readonly showSlotUnSubscribeFn;
    private _active;
    constructor(element: ElementRef, yjQuery: JQueryStatic, systemEventService: SystemEventService, contextualMenuService: IContextualMenuService, nodeUtils: NodeUtils, crossFrameEventService: CrossFrameEventService);
    ngOnChanges(changes: SimpleChanges): void;
    ngOnInit(): void;
    ngDoCheck(): void;
    ngOnDestroy(): void;
    updateItems(): void;
    triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void;
    private hidePadding;
    private getRightMostOffsetFromPage;
    private positionPanelHorizontally;
    private positionPanelVertically;
}
