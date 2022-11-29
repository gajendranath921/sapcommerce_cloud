/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InlineHelpModule } from '@fundamental-ngx/core';
import {
    SelectModule,
    SharedComponentsModule,
    TooltipModule,
    TranslationModule
} from 'smarteditcommons';
import { PersonalizationsmarteditSharedComponentsModule } from '../commonComponents/PersonalizationsmarteditSharedComponentsModule';
import { ComponentDropdownItemPrinterComponent } from './ComponentDropdownItemPrinterComponent';
import { ContextMenuDeleteActionComponent } from './ContextMenuDeleteActionComponent';
import { PersonalizationsmarteditContextMenuAddEditActionComponent } from './PersonalizationsmarteditContextMenuAddEditActionComponent';

/**
 * @ngdoc overview
 * @name PersonalizationsmarteditContextMenuModule
 */

@NgModule({
    imports: [
        CommonModule,
        TranslationModule.forChild(),
        SharedComponentsModule,
        InlineHelpModule,
        SelectModule,
        TooltipModule,
        PersonalizationsmarteditSharedComponentsModule
    ],
    declarations: [
        PersonalizationsmarteditContextMenuAddEditActionComponent,
        ComponentDropdownItemPrinterComponent,
        ContextMenuDeleteActionComponent
    ],
    entryComponents: [
        PersonalizationsmarteditContextMenuAddEditActionComponent,
        ComponentDropdownItemPrinterComponent,
        ContextMenuDeleteActionComponent
    ],
    exports: [
        PersonalizationsmarteditContextMenuAddEditActionComponent,
        ComponentDropdownItemPrinterComponent
    ]
})
export class PersonalizationsmarteditContextMenuModule {}
