import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import { CollapsibleContainerApi, FetchPageStrategy, Nullable, SmarteditRoutingService, SystemEventService, WorkflowService, Workflow, WorkflowAction, WorkflowActionComment, WorkflowDecision } from 'smarteditcommons';
import { WorkflowFacade } from '../../../services';
export declare const PAGE_APPROVAL_PAGE_APPROVED_ID = "PageApprovalPageApproved";
export declare class WorkflowActionItemComponent implements OnInit, OnDestroy {
    private readonly workflowFacade;
    private readonly cMSTimeService;
    private readonly systemEventService;
    private readonly routingService;
    private readonly cdr;
    private readonly workflowService;
    workflow: Workflow;
    workflowAction: WorkflowAction;
    canMakeDecisions: boolean;
    isMenuOpen: boolean;
    hasComments: boolean;
    pageSize: number;
    workflowActionComments: WorkflowActionComment[];
    showApprovalInfo: boolean;
    approvalInfo: string;
    private collapsibleContainerApi;
    private unRegWorkflowMenuOpenedEvent;
    constructor(workflowFacade: WorkflowFacade, cMSTimeService: CMSTimeService, systemEventService: SystemEventService, routingService: SmarteditRoutingService, cdr: ChangeDetectorRef, workflowService: WorkflowService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    fetchPageOfComments: FetchPageStrategy<WorkflowActionComment>;
    onCommentsLoaded(comments: WorkflowActionComment[]): void;
    setCollapsibleContainerApi($api: CollapsibleContainerApi): void;
    getWorkflowActionStatusClass(): Nullable<string>;
    getReadableStatus(): string;
    getActiveSince(): Nullable<string>;
    canShowDecisionButtons(): boolean;
    canShowComments(): boolean;
    onMainButtonClick($event: MouseEvent, decision: WorkflowDecision): Promise<void>;
    onSplitButtonClick($event: MouseEvent): void;
    onMenuHide(): void;
    trackByIndex(index: number): number;
    private loadCommentsAndSetHasCommentsFlag;
    private loadComments;
    private makeDecision;
    private onOtherMenuOpening;
}