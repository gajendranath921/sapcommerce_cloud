import { ChangeDetectorRef } from '@angular/core';
import { ISyncStatus } from 'cmscommons';
import { AbstractDecorator, CrossFrameEventService, ICatalogService, IPageInfoService } from 'smarteditcommons';
import { SlotSynchronizationService } from '../../services/SlotSynchronizationService';
export declare class SyncIndicatorDecorator extends AbstractDecorator {
    private catalogService;
    private slotSynchronizationService;
    private crossFrameEventService;
    private pageInfoService;
    private cdr;
    pageUUID: string;
    syncStatus: ISyncStatus;
    isVersionNonActive: boolean;
    private unRegisterSyncPolling;
    constructor(catalogService: ICatalogService, slotSynchronizationService: SlotSynchronizationService, crossFrameEventService: CrossFrameEventService, pageInfoService: IPageInfoService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    private getPageUuid;
    private getIsVersionNonActive;
    private fetchSyncStatus;
}
