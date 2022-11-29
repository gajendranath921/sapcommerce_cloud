/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { SeDowngradeComponent } from 'smarteditcommons';

export interface CMSComponentTypeInfo {
    code: string;
    i18nKey: string;
    name: string;
    technicalUniqueId?: string;
}

@SeDowngradeComponent()
@Component({
    selector: 'se-component-type',
    templateUrl: './ComponentTypeComponent.html',
    styleUrls: ['./component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentTypeComponent {
    @Input() typeInfo: CMSComponentTypeInfo;
}
