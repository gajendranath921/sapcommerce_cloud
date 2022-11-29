import { EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ISynchronizationPanelApi, ISyncJob, ISyncStatus, ISyncStatusItem } from 'cmscommons';
import { HomepageService } from 'cmssmarteditcontainer/services';
import { CrossFrameEventService, IUriContext, TypedMap, IPageService, ICMSPage, ISyncPollingService } from 'smarteditcommons';
export declare class PageSynchronizationPanelComponent implements OnInit, OnDestroy {
    private pageService;
    private homepageService;
    private crossFrameEventService;
    private syncPollingService;
    private translateService;
    selectedItemsUpdate: EventEmitter<ISyncStatusItem[]>;
    uriContext: IUriContext;
    cmsPage: ICMSPage;
    showFooter: boolean;
    private synchronizationPanel;
    private syncStatus;
    private synchronizationPanelApi;
    private pageSyncConditions;
    private unSubPageUpdatedEvent;
    constructor(pageService: IPageService, homepageService: HomepageService, crossFrameEventService: CrossFrameEventService, syncPollingService: ISyncPollingService, translateService: TranslateService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    getSyncStatus: () => Promise<ISyncStatus>;
    performSync: (items: TypedMap<string>[]) => Promise<ISyncJob>;
    onGetApi(api: ISynchronizationPanelApi): void;
    onSyncStatusReady(syncStatus: ISyncStatus): void;
    onSelectedItemsUpdate(items: ISyncStatusItem[]): void;
    syncItems(): Promise<void>;
    private syncStatusReady;
    private evaluateIfSyncIsApproved;
    private disablePageSync;
    private hidePageSync;
    private showPageSync;
    private enableSlotsSync;
}
