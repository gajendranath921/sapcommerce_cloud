import { ElementRef, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { IIframeClickDetectionService } from '../../services/interfaces/IIframeClickDetectionService';
export declare class ClickOutsideDirective implements OnInit, OnDestroy {
    private document;
    private host;
    private iframeClickDetectionService;
    clickOutside: EventEmitter<void>;
    private readonly id;
    constructor(document: Document, host: ElementRef, iframeClickDetectionService: IIframeClickDetectionService);
    onDocumentClick(target: HTMLElement): void;
    ngOnInit(): void;
    ngOnDestroy(): void;
    private ignoreNotExistingTarget;
    private onOutsideClick;
}
