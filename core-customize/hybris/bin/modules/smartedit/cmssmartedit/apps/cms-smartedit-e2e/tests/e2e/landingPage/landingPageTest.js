/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
var landingPage = e2e.pageObjects.landingPage;
var pageList = e2e.pageObjects.PageList;
var navigationTree = e2e.componentObjects.navigationTree;
var backendClient = e2e.mockBackendClient;
const testTimestamp = '1/29/16 4:25 PM';

describe('Landing page - GIVEN I am on the landing page ', function () {
    beforeEach(function () {
        browser.bootstrap();
    });

    beforeEach(function () {
        browser.executeScript(
            'window.sessionStorage.setItem("syncPermissions", arguments[0])',
            JSON.stringify({
                canSynchronize: true,
                targetCatalogVersion: 'Online'
            })
        );
    });

    it('WHEN I click on the page list button ' + 'THEN the page list is loaded', function () {
        // WHEN
        landingPage.actions.navigateToCatalogPageList(
            landingPage.constants.APPAREL_UK_CATALOG,
            landingPage.constants.ACTIVE_CATALOG_VERSION
        );
        // THEN
        pageList.assertions.assertPageListIsDisplayed();
    });

    it(
        'WHEN I click on the navigation management button ' +
            'THEN the navigation management page is loaded',
        function () {
            // WHEN
            landingPage.actions.navigateToCatalogNavigationManagementPage(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.ACTIVE_CATALOG_VERSION
            );

            // THEN
            navigationTree.assertNavigationManagementPageIsDisplayed();
        }
    );

    it(
        'WHEN the page is fully loaded ' +
            'THEN I expect the sync information to be retrieved for each of the displayed catalog versions',
        function () {
            // GIVEN
            var activeCatalogSyncInfo = {
                timestamp: testTimestamp,
                fromVersion: landingPage.constants.STAGED_CATALOG_VERSION,
                status: landingPage.constants.SYNC_STATUS_FINISHED,
                buttonIsVisible: false,
                buttonIsEnabled: false
            };

            var stagedCatalogSyncInfo = {
                timestamp: testTimestamp,
                status: landingPage.constants.SYNC_STATUS_FINISHED,
                buttonIsVisible: true,
                buttonIsEnabled: true
            };

            // THEN
            landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.ACTIVE_CATALOG_VERSION,
                activeCatalogSyncInfo
            );
            landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.STAGED_CATALOG_VERSION,
                stagedCatalogSyncInfo
            );
        }
    );

    it(
        'WHEN I click on the sync button ' +
            'THEN I expect the sync item to show that a sync job is in progress',
        function () {
            // GIVEN
            var activeCatalogSyncInfo = {
                status: landingPage.constants.SYNC_STATUS_IN_PROGRESS,
                buttonIsVisible: false
            };

            var stagedCatalogSyncInfo = {
                status: landingPage.constants.SYNC_STATUS_IN_PROGRESS,
                buttonIsVisible: true,
                buttonIsEnabled: false
            };

            // WHEN
            landingPage.actions.synchronizeCatalogVersion(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.STAGED_CATALOG_VERSION
            );

            // THEN
            landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.ACTIVE_CATALOG_VERSION,
                activeCatalogSyncInfo
            );
            landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.STAGED_CATALOG_VERSION,
                stagedCatalogSyncInfo
            );
        }
    );

    it(
        'WHEN I click on the sync button ' +
            'THEN I expect the sync button not to be enabled until the current sync is finished',
        function () {
            // GIVEN
            var activeCatalogSyncInfo = {
                timestamp: testTimestamp,
                fromVersion: landingPage.constants.STAGED_CATALOG_VERSION,
                status: landingPage.constants.SYNC_STATUS_FINISHED,
                buttonIsVisible: false
            };

            var stagedCatalogSyncInfo = {
                timestamp: testTimestamp,
                status: landingPage.constants.SYNC_STATUS_FINISHED,
                buttonIsVisible: true,
                buttonIsEnabled: true
            };

            // WHEN
            landingPage.actions.synchronizeCatalogVersion(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.STAGED_CATALOG_VERSION
            );
            landingPage.utils.markSyncAsComplete().then(function () {
                // THEN
                landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                    landingPage.constants.APPAREL_UK_CATALOG,
                    landingPage.constants.ACTIVE_CATALOG_VERSION,
                    activeCatalogSyncInfo
                );
                landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                    landingPage.constants.APPAREL_UK_CATALOG,
                    landingPage.constants.STAGED_CATALOG_VERSION,
                    stagedCatalogSyncInfo
                );
            });
        }
    );

    it(
        'WHEN I click on the sync button AND the sync job fails ' +
            'THEN I expect to see the failure message and the button re-enabled to allow another sync request',
        function () {
            // GIVEN
            var activeCatalogSyncInfo = {
                status: landingPage.constants.SYNC_STATUS_FAILED,
                buttonIsVisible: false
            };

            var stagedCatalogSyncInfo = {
                status: landingPage.constants.SYNC_STATUS_FAILED,
                buttonIsVisible: true,
                buttonIsEnabled: true
            };

            // WHEN
            landingPage.actions.synchronizeCatalogVersion(
                landingPage.constants.APPAREL_UK_CATALOG,
                landingPage.constants.STAGED_CATALOG_VERSION
            );
            landingPage.utils.markSyncAsFailed().then(function () {
                // THEN
                landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                    landingPage.constants.APPAREL_UK_CATALOG,
                    landingPage.constants.ACTIVE_CATALOG_VERSION,
                    activeCatalogSyncInfo
                );
                landingPage.assertions.catalogVersionSyncWidgetHasRightInfo(
                    landingPage.constants.APPAREL_UK_CATALOG,
                    landingPage.constants.STAGED_CATALOG_VERSION,
                    stagedCatalogSyncInfo
                );
            });
        }
    );
});

describe('Landing page without sync permissions', function () {
    let fixtureID0;

    beforeAll(async function () {
        fixtureID0 = await backendClient.modifyFixture(
            ['permissionswebservices\\/v1\\/permissions\\/catalogs\\/search'],
            {
                'permissionsList.0.syncPermissions': [{}]
            }
        );
    });

    beforeEach(function () {
        browser.bootstrap();
        browser.executeScript(
            'window.sessionStorage.setItem("syncPermissions", arguments[0])',
            JSON.stringify({})
        );
    });

    it('GIVEN the landing page which has no sync permissions THEN sync toolbar menu button should not be displayed', function () {
        //GIVEN

        var stagedCatalogSyncInfo = {
            timestamp: testTimestamp,
            status: landingPage.constants.SYNC_STATUS_FINISHED,
            buttonIsVisible: false
        };

        //THEN
        landingPage.assertions.syncButtonIsDisplayedIfNecessary(
            landingPage.constants.APPAREL_UK_CATALOG,
            landingPage.constants.STAGED_CATALOG_VERSION,
            stagedCatalogSyncInfo
        );
    });

    afterAll(function () {
        backendClient.removeFixture(fixtureID0);
    });
});
