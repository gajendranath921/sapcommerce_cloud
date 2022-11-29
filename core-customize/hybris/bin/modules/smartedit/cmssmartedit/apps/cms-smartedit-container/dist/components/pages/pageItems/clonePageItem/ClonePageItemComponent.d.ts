import { OnInit } from '@angular/core';
import { MultiNamePermissionContext, IDropdownMenuItemData, ICMSPage, UserTrackingService } from 'smarteditcommons';
import { ClonePageWizardService } from '../../clonePageWizard';
export declare class ClonePageItemComponent implements OnInit {
    private dropdownMenuData;
    private clonePageWizardService;
    private userTrackingService;
    pageInfo: ICMSPage;
    clonePagePermission: MultiNamePermissionContext[];
    constructor(dropdownMenuData: IDropdownMenuItemData, clonePageWizardService: ClonePageWizardService, userTrackingService: UserTrackingService);
    ngOnInit(): void;
    onClickOnClone(): void;
}
