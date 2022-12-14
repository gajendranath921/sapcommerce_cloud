/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IComponentType } from '../../entities/components/componentType.entity';

export const productPage: IComponentType = {
    attributes: [
        {
            cmsStructureType: 'ShortString',
            i18nKey: 'type.abstractpage.uid.name',
            localized: false,
            qualifier: 'uid'
        },
        {
            cmsStructureType: 'ShortString',
            i18nKey: 'type.abstractpage.name.name',
            localized: false,
            qualifier: 'name'
        },
        {
            cmsStructureType: 'DateTime',
            i18nKey: 'type.abstractpage.creationtime.name',
            localized: false,
            qualifier: 'creationtime'
        },
        {
            cmsStructureType: 'DateTime',
            i18nKey: 'type.abstractpage.modifiedtime.name',
            localized: false,
            qualifier: 'modifiedtime'
        },
        {
            cmsStructureType: 'ShortString',
            i18nKey: 'type.abstractpage.title.name',
            localized: true,
            qualifier: 'title'
        },
        {
            cmsStructureType: 'PageRestrictionsEditor',
            i18nKey: 'type.abstractpage.restrictions.name',
            qualifier: 'restrictions'
        }
    ],
    category: 'PAGE',
    code: 'MockPage',
    i18nKey: 'type.mockpage.name',
    name: 'Mock Page'
};
