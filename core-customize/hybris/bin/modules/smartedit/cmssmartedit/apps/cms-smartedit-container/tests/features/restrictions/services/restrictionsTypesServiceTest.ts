/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { RestrictionTypesRestService } from 'cmssmarteditcontainer/dao/RestrictionTypesRestService';
import { PageTypesRestrictionTypesService } from 'cmssmarteditcontainer/services/pageRestrictions/PageTypesRestrictionTypesService';
import { RestrictionTypesService } from 'cmssmarteditcontainer/services/pageRestrictions/RestrictionTypesService';

describe('restrictionTypesService', () => {
    let restrictionTypesService: RestrictionTypesService;
    let pageTypesRestrictionTypesService: jasmine.SpyObj<PageTypesRestrictionTypesService>;
    let restrictionTypesRestService: jasmine.SpyObj<RestrictionTypesRestService>;

    const restrictionTypeMocks = {
        restrictionTypes: [
            {
                code: 'CMSTimeRestriction',
                name: {
                    de: 'DAS blabla',
                    en: 'Time Restriction'
                }
            },
            {
                code: 'CMSCatalogRestriction',
                name: {
                    en: 'Catalog Restriction'
                }
            },
            {
                code: 'CMSCategoryRestriction',
                name: {
                    en: 'category Restriction'
                }
            },
            {
                code: 'CMSUserRestriction',
                name: {
                    en: 'User Restriction'
                }
            }
        ]
    };

    const restrictionTypesCodesForContentPageType = [
        'CMSTimeRestriction',
        'CMSUserRestriction',
        'CMSUserGroupRestriction',
        'CMSUiExperienceRestriction'
    ];

    const restrictionTypesForContentPageType = [
        {
            code: 'CMSTimeRestriction',
            name: {
                de: 'DAS blabla',
                en: 'Time Restriction'
            }
        },
        {
            code: 'CMSUserRestriction',
            name: {
                en: 'User Restriction'
            }
        }
    ];

    const catalogRestrictionType = {
        code: 'CMSCatalogRestriction',
        name: {
            en: 'Catalog Restriction'
        }
    };

    beforeEach(() => {
        restrictionTypesRestService = jasmine.createSpyObj('restrictionTypesRestService', [
            'getRestrictionTypes'
        ]);
        restrictionTypesRestService.getRestrictionTypes.and.callFake(() => {
            return Promise.resolve(restrictionTypeMocks);
        });

        pageTypesRestrictionTypesService = jasmine.createSpyObj(
            'pageTypesRestrictionTypesService',
            ['getRestrictionTypeCodesForPageType']
        );
        pageTypesRestrictionTypesService.getRestrictionTypeCodesForPageType.and.callFake(() => {
            return Promise.resolve(restrictionTypesCodesForContentPageType);
        });

        restrictionTypesService = new RestrictionTypesService(
            pageTypesRestrictionTypesService,
            restrictionTypesRestService
        );
    });

    // ------------------------------------------------------------------------------------------

    it('should return all restriction types', async () => {
        const result = await restrictionTypesService.getRestrictionTypes();

        expect(result).toEqual(restrictionTypeMocks.restrictionTypes);
    });

    it('should cache the results and return cache if it exists', async () => {
        const orig = await restrictionTypesService.getRestrictionTypes();
        const second = await restrictionTypesService.getRestrictionTypes();

        expect(restrictionTypesRestService.getRestrictionTypes.calls.count()).toBe(1);
        expect(orig).toEqual(second);
    });

    it('should return restriction types for specific page type', async () => {
        const result = await restrictionTypesService.getRestrictionTypesByPageType('ContentPage');

        expect(result).toEqual(restrictionTypesForContentPageType);
    });

    it('should get a restriction type object for to given type code', async () => {
        const result = await restrictionTypesService.getRestrictionTypeForTypeCode(
            'CMSCatalogRestriction'
        );
        expect(result).toEqual(catalogRestrictionType);
    });
});
