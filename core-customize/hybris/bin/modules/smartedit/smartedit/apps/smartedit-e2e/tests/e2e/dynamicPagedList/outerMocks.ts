/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';

import { moduleUtils, urlUtils, HttpBackendService, SeEntryModule } from 'smarteditcommons';

import { OuterAuthorizationMocks } from '../../utils/commonMockedModules/outerAuthorizationMock';
import { i18nMocks } from '../../utils/commonMockedModules/outerI18nMock';
import { OuterLanguagesMocks } from '../../utils/commonMockedModules/outerLanguagesMock';
import { OuterOtherMocks } from '../../utils/commonMockedModules/outerOtherMock';
import { OuterPermissionMocks } from '../../utils/commonMockedModules/outerPermissionMocks';
import { OuterPreviewMocks } from '../../utils/commonMockedModules/outerPreviewMock';
import { OuterSitesMocks } from '../../utils/commonMockedModules/outerSitesMock';
import { OuterWhoAmIMocks } from '../../utils/commonMockedModules/outerWhoAmIMocks';
import '../../utils/commonMockedModules/outerGlobalBasePathFetchMock';

@SeEntryModule('OuterMocks')
@NgModule({
    imports: [
        OuterAuthorizationMocks,
        OuterWhoAmIMocks,
        i18nMocks,
        OuterLanguagesMocks,
        OuterOtherMocks,
        OuterPreviewMocks,
        OuterSitesMocks,
        OuterPermissionMocks
    ],
    providers: [
        moduleUtils.bootstrap(
            (httpBackendService: HttpBackendService) => {
                const map = [
                    {
                        value: '"thepreviewTicketURI"',
                        key: 'previewTicketURI'
                    },
                    {
                        value: '"/cmswebservices/v1/i18n/languages"',
                        key: 'i18nAPIRoot'
                    },
                    {
                        value:
                            '{"smartEditContainerLocation":"/apps/smartedit-e2e/generated/e2e/dynamicPagedList/outerapp.js"}',
                        key: 'applications.OuterApp'
                    }
                ];

                const items: any[] = [];
                // create DynamicPagedListDropdownItem that are rendered by DataTableRendererComponent
                const makeItems = () => {
                    for (let i = 1; i <= 1000; i++) {
                        items.push({
                            name: 'item-' + i,
                            typeCode: 'itemTypeCode-' + i,
                            uid: 'item-' + i
                        });
                    }
                };

                makeItems();

                httpBackendService
                    .whenGET(/\/pagedItems/)
                    .respond(function (method, url, data, headers) {
                        const params = urlUtils.parseQuery(data) as any;
                        const currentPage = params.currentPage;
                        const pageSize = params.pageSize;
                        const sort = params.sort;

                        let filteredItems = items.filter(function (item) {
                            return params.mask
                                ? (item.name &&
                                      typeof item.name === 'string' &&
                                      item.name.toUpperCase().indexOf(params.mask.toUpperCase()) >
                                          -1) ||
                                      item.uid.toUpperCase().indexOf(params.mask.toUpperCase()) > -1
                                : true;
                        });

                        filteredItems = sortFilteredItems(sort, filteredItems);
                        const results = filteredItems.slice(
                            currentPage * pageSize,
                            currentPage * pageSize + parseInt(pageSize, 10)
                        );

                        const pagedResults = {
                            pagination: {
                                count: pageSize,
                                page: currentPage,
                                totalCount: filteredItems.length
                            },
                            results
                        };

                        return [200, pagedResults];
                    });

                httpBackendService.whenGET(/smartedit\/configuration/).respond(() => {
                    return [200, map];
                });
            },
            [HttpBackendService]
        )
    ]
})
export class OuterMocks {}

window.pushModules(OuterMocks);
function sortFilteredItems(sort: string, filteredItems: any[]): any[] {
    if (sort) {
        const sortdirection = sort.split(':')[1];

        return filteredItems.sort(function (a, b) {
            const nameA = a.name.toUpperCase(); // ignore upper and lowercase
            const nameB = b.name.toUpperCase(); // ignore upper and lowercase
            if (nameA < nameB) {
                return sortdirection === 'asc' ? -1 : 1;
            }
            if (nameA > nameB) {
                return sortdirection === 'asc' ? 1 : -1;
            }
            return 0;
        });
    }
    return filteredItems;
}
