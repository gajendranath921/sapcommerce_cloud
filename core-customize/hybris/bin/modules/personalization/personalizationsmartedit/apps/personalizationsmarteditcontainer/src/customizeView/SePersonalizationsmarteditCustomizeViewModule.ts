/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { PersonalizationsmarteditCommonsComponentsModule } from 'personalizationcommons';
import { PersonalizationsmarteditSharedComponentsModule } from 'personalizationsmarteditcontainer/commonComponents/PersonalizationsmarteditSharedComponentsModule';
import { CustomizationsListComponent } from 'personalizationsmarteditcontainer/customizeView/customizationsList/CustomizationsListComponent';
import { CustomizeViewComponent } from 'personalizationsmarteditcontainer/customizeView/CustomizeViewComponent';
import { CustomizeViewServiceProxy } from 'personalizationsmarteditcontainer/customizeView/CustomizeViewServiceOuterProxy';
import { PersonalizationsmarteditDataFactoryModule } from 'personalizationsmarteditcontainer/dataFactory';
import { SePersonalizationsmarteditServicesModule } from 'personalizationsmarteditcontainer/service';
import { TranslationModule, SharedComponentsModule } from 'smarteditcommons';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        TranslationModule.forChild(),
        SharedComponentsModule,
        SePersonalizationsmarteditServicesModule,
        PersonalizationsmarteditDataFactoryModule,
        PersonalizationsmarteditSharedComponentsModule,
        PersonalizationsmarteditCommonsComponentsModule
    ],
    declarations: [CustomizationsListComponent, CustomizeViewComponent],
    entryComponents: [CustomizationsListComponent, CustomizeViewComponent],
    providers: [CustomizeViewServiceProxy],
    exports: []
})
export class SePersonalizationsmarteditCustomizeViewModule {}
