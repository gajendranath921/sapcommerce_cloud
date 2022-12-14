/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FormSchema, Payload } from '@smart/utils';

import { GenericEditorDynamicFieldComponent } from '../components/dynamicField/GenericEditorDynamicFieldComponent';
import { GenericEditorField } from '../types';
import { createValidatorMap, SchemaDataMapper } from './RootSchemaDataMapper';

/**
 * A schema and data mapper for the GenericEditorDynamicFieldComponent.
 */
export class FieldSchemaDataMapper implements SchemaDataMapper {
    constructor(
        public id: string,
        private structure: GenericEditorField,
        private required: boolean,
        private component: Payload
    ) {}

    toValue(): any {
        return this.component[this.id];
    }

    toSchema(): FormSchema {
        return {
            type: 'field',
            component: 'field',
            validators: {
                required: (this.required && this.structure.editable) || undefined, // Has to be required and editable.
                ...createValidatorMap(
                    this.structure.validators,
                    this.id,
                    this.structure,
                    this.required,
                    this.component
                )
            },
            inputs: {
                id: this.id,
                field: this.structure,
                qualifier: this.id,
                component: this.component
            } as Partial<GenericEditorDynamicFieldComponent>
        };
    }
}
