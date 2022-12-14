/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = (function () {
    var PageStructureMocks = (function () {
        function restPayload() {
            return {
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
                        cmsStructureType: 'ShortString',
                        i18nKey: 'type.contentpage.label.name',
                        localized: false,
                        qualifier: 'label'
                    }
                ],
                category: 'PAGE',
                code: 'MockPage',
                i18nKey: 'type.mockpage.name',
                name: 'Mock Page'
            };
        }

        function restPayloadWithReadOnlyField() {
            return {
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
                        cmsStructureType: 'ShortString',
                        i18nKey: 'type.contentpage.label.name',
                        localized: false,
                        qualifier: 'label',
                        editable: false
                    }
                ],
                category: 'PAGE',
                code: 'MockPage',
                i18nKey: 'type.mockpage.name',
                name: 'Mock Page'
            };
        }

        return {
            getFields() {
                return restPayload().attributes;
            },
            getFieldsWithReadOnly() {
                return restPayloadWithReadOnlyField().attributes;
            }
        };
    })();

    return PageStructureMocks;
})();
