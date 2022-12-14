/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
var genericEditor;
if (typeof require !== 'undefined') {
    genericEditor = require('./genericEditorComponentObject');
}

const ITEM_CSS = '.se-version-item-menu__item';

module.exports = (function () {
    var componentObject = {};

    // --------------------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------------------
    var PAGE_VERSIONS = [
        'Version 1',
        'New - Version 2',
        'New - Version 3',
        'Other - Version 4',
        'Version 5 - This one has a super loooooong label which should be handled properly',
        'New - Version 6',
        'Other - Version 7',
        'Version 8',
        'Version 9',
        'Version 10',
        'Version 11',
        'Special - Version 12',
        'Version 13',
        'Version 14',
        'Version 15'
    ];

    // --------------------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------------------
    componentObject.constants = {
        SEARCH_DEBOUNCE_TIME: 1000,
        PAGE_SIZE: 10 // aka BATCH size.
    };

    // --------------------------------------------------------------------------------------------------
    // Elements
    // --------------------------------------------------------------------------------------------------
    componentObject.elements = {
        // Toolbar
        getPageVersionsMenuButton: function () {
            return browser.findElement(by.css('[data-item-key="se.cms.pageVersionsMenu"] button'));
        },
        getPageVersionsMenu: function () {
            return browser.findElement(
                by.css('[data-item-key="se.cms.pageVersionsMenu"] se-page-version-menu')
            );
        },
        getPageVersionToolbarContextSelector: function () {
            return by.css('.se-template-toolbar__context-template .se-version-item-context');
        },
        getPageVersionToolbarContext: function () {
            return browser.findElement(this.getPageVersionToolbarContextSelector());
        },
        getPageVersionLabelInToolbarContext: function () {
            return this.getPageVersionToolbarContext()
                .element(by.css('.se-version-item-context__info-label'))
                .getText();
        },
        getRemovePageVersionButton: function () {
            return this.getPageVersionToolbarContext().element(
                by.css('.se-version-item-context__remove-btn')
            );
        },
        getPageVersionDescriptionSelector: function () {
            return by.css('.se-version-item-context__description');
        },
        getPageVersionDescription: function () {
            return browser.findElement(this.getPageVersionDescriptionSelector());
        },
        getCreateVersionMenuButtonSelector: function () {
            return by.id('smartEditPerspectiveToolbar_option_se.cms.createVersionMenu_btn');
        },
        getCreateVersionMenuButton: function () {
            return browser.findElement(this.getCreateVersionMenuButtonSelector());
        },
        getRollbackVersionMenuButtonSelector: function () {
            return by.id('smartEditPerspectiveToolbar_option_se.cms.rollbackVersionMenu_btn');
        },
        getRollbackVersionMenuButton: function () {
            return browser.findElement(this.getRollbackVersionMenuButtonSelector());
        },

        // Manage Versions Button and Link
        getManageVersionsButton: function () {
            return this.getPageVersionsMenu().element(by.css('.se-versions-panel__manage-btn'));
        },
        getManageVersionsLink: function () {
            return this.getPageVersionsMenu().element(by.css('.se-versions-panel__manage-link'));
        },

        // Search Section
        getVersionsCount: function () {
            return this.getPageVersionsMenu()
                .element(by.css('.se-versions-panel__count'))
                .getText();
        },
        getSearchBox: function () {
            return this.getPageVersionsMenu().element(
                by.css('.se-versions-panel__search .se-input-group__input-area')
            );
        },

        // Versions List
        getEmptyVersionListMsg: function () {
            return this.getPageVersionsMenu().element(by.css('.se-versions-panel__empty-list'));
        },
        getVersionsList: function () {
            return this.getPageVersionsMenu().element(
                by.css('.se-versions-panel__infinite-scroll')
            );
        },
        getPageVersions: function () {
            var pageVersionsSelector = by.css('.se-version-item .se-version-item__label');

            return element.all(pageVersionsSelector).then(
                function (pageVersions) {
                    return this.getPageVersionsLabels(pageVersions);
                }.bind(this)
            );
        },
        getPageVersionsLabels: function (pageVersions) {
            return protractor.promise.all(
                pageVersions.map(function (item) {
                    return item.getText();
                })
            );
        },
        getPageVersionsScrollElement: function () {
            return browser.findElement(by.css('.se-versions-panel__infinite-scroll > div'));
        },
        getPageVersionByLabel: function (label) {
            return browser.findElement(by.css('.se-version-item__header[title="' + label + '"]'));
        },
        getPageVersionLinkByLabel: function (label) {
            return this.getPageVersionByLabel(label).element(by.css('.se-version-item__label'));
        },
        getPageVersionMenuByLabel: function (label) {
            return this.getPageVersionByLabel(label).element(by.css('se-version-item-menu'));
        },
        getDeleteMenuItem: function () {
            return element(by.cssContainingText(ITEM_CSS, 'Delete'));
        },
        getEditMenuItem: function () {
            return element(by.cssContainingText(ITEM_CSS, 'Edit Details'));
        },
        getViewMenuItem: function () {
            return element(by.cssContainingText(ITEM_CSS, 'View'));
        },
        getRollbackMenuItem: function () {
            return element(by.cssContainingText(ITEM_CSS, 'Rollback'));
        },
        getRollbackSuccessAlert: function () {
            return element(by.css('fd-alert.fd-alert--success'));
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Actions
    // --------------------------------------------------------------------------------------------------
    componentObject.actions = {
        openMenu: function () {
            browser.waitUntilNoModal();
            return browser.click(componentObject.elements.getPageVersionsMenuButton());
        },
        clickManageVersionsButton: function () {
            return browser.click(componentObject.elements.getManageVersionsButton());
        },
        scrollPageVersions: function (pages) {
            var collectionSize = PAGE_VERSIONS.length;
            var expectedNumVersions = Math.min(
                pages * componentObject.constants.PAGE_SIZE,
                collectionSize
            );

            var currentNumOfVersions = 0;

            return browser.waitUntil(function () {
                return componentObject.elements.getPageVersions().then(function (versions) {
                    if (versions.length > currentNumOfVersions) {
                        currentNumOfVersions = versions.length;
                        browser.scrollToBottom(
                            componentObject.elements.getPageVersionsScrollElement()
                        );
                    }

                    return versions.length >= expectedNumVersions;
                });
            }, 'failed to scroll all page versions');
        },
        searchVersion: function (searchTerm) {
            return browser
                .sendKeys(
                    componentObject.elements.getSearchBox(),
                    searchTerm,
                    'Cannot find page versions search box.'
                )
                .then(function () {
                    return browser.sleep(componentObject.constants.SEARCH_DEBOUNCE_TIME);
                });
        },
        selectPageVersionByLabel: function (pageVersionLabel) {
            return browser
                .click(componentObject.elements.getPageVersionLinkByLabel(pageVersionLabel))
                .then(function () {
                    browser.waitUntilNoModal();
                    return browser.waitForPresence(
                        componentObject.elements.getPageVersionToolbarContextSelector()
                    );
                });
        },
        removeSelectedPageVersion: function () {
            return browser.click(componentObject.elements.getRemovePageVersionButton());
        },
        hoverOverPageVersionToolbarContext: function () {
            return browser.hoverElement(componentObject.elements.getPageVersionToolbarContext());
        },
        openItemMenuByLabel: function (pageVersionLabel) {
            return browser.click(
                componentObject.elements.getPageVersionMenuByLabel(pageVersionLabel)
            );
        },
        clickDeleteMenuItem: function () {
            return browser.click(componentObject.elements.getDeleteMenuItem());
        },
        clickEditMenuItem: function () {
            return browser.click(componentObject.elements.getEditMenuItem());
        },
        clickViewMenuItem: function () {
            return browser.click(componentObject.elements.getViewMenuItem());
        },
        clickRollbackMenuItem: function () {
            return browser.click(componentObject.elements.getRollbackMenuItem());
        },

        // Create Version
        clickCreateVersionMenuButton: function () {
            return browser.click(componentObject.elements.getCreateVersionMenuButton());
        },
        waitForEditorModalWithTitleToBeClosed: function (titleI18nKey) {
            genericEditor.actions.waitForEditorModalWithTitleToBeClosed(titleI18nKey);
            browser.waitUntilNoModal();
        },

        // Rollback Version
        clickRollbackVersionMenuButton: function () {
            browser.switchToParent();
            return browser.click(componentObject.elements.getRollbackVersionMenuButton());
        },

        // Delete Page Version
        deletePageVersion: function (pageVersionLabel) {
            return componentObject.actions.openItemMenuByLabel(pageVersionLabel).then(function () {
                return componentObject.actions.clickDeleteMenuItem();
            });
        },

        // Edit Page Version
        clickEditVersionItemMenuButton: function (pageVersionLabel) {
            return componentObject.actions.openItemMenuByLabel(pageVersionLabel).then(function () {
                return componentObject.actions.clickEditMenuItem();
            });
        },

        // View Page Version
        clickViewVersionItemMenuButton: function (pageVersionLabel) {
            return componentObject.actions.openItemMenuByLabel(pageVersionLabel).then(function () {
                return componentObject.actions.clickViewMenuItem();
            });
        },

        // View Rollback Version
        clickRollbackVersionItemMenuButton: function (pageVersionLabel) {
            return componentObject.actions.openItemMenuByLabel(pageVersionLabel).then(function () {
                return componentObject.actions.clickRollbackMenuItem();
            });
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Assertions
    // --------------------------------------------------------------------------------------------------
    componentObject.assertions = {
        // Manage Versions Button
        manageVersionsButtonIsDisplayed: function () {
            expect(browser.isPresent(componentObject.elements.getManageVersionsButton())).toBe(
                true,
                "Expected 'Manage' button to be present."
            );
        },
        manageVersionsButtonIsNotDisplayed: function () {
            expect(componentObject.elements.getManageVersionsButton()).toBeAbsent();
        },

        // Manage Versions Link
        manageVersionsLinkIsDisplayed: function () {
            expect(browser.isPresent(componentObject.elements.getManageVersionsLink())).toBe(
                true,
                "Expected 'Manage Versions' link to be present."
            );
        },
        manageVersionsLinkIsNotDisplayed: function () {
            expect(componentObject.elements.getManageVersionsLink()).toBeAbsent();
        },

        // Versions List
        emptyListMessageIsDisplayed: function () {
            expect(browser.isPresent(componentObject.elements.getEmptyVersionListMsg())).toBe(
                true,
                "Expected 'empty version list' message to be displayed."
            );
        },
        versionsListIsNotDisplayed: function () {
            expect(componentObject.elements.getVersionsList()).toBeAbsent();
        },
        versionsListIsEmpty: function () {
            this.emptyListMessageIsDisplayed();
            this.versionsListIsNotDisplayed();
        },
        emptyListMessageIsNotDisplayed: function () {
            expect(componentObject.elements.getEmptyVersionListMsg()).toBeAbsent();
        },
        versionsListIsDisplayed: function () {
            expect(browser.isPresent(componentObject.elements.getVersionsList())).toBe(
                true,
                'Expected version list to be displayed.'
            );
        },
        versionsListIsNotEmpty: function () {
            this.emptyListMessageIsNotDisplayed();
            this.versionsListIsDisplayed();
        },
        menuHasExpectedPageVersions: function (searchTerm, pagesLoaded) {
            var filteredResultList = componentObject.utils.filterPageVersions(searchTerm);
            var expectedVersionsInMenu = componentObject.utils.getExpectedVersionsLoaded(
                filteredResultList,
                pagesLoaded
            );

            expect(componentObject.elements.getVersionsCount()).toBe(
                filteredResultList.length + ' Versions'
            );
            expect(componentObject.elements.getPageVersions()).toEqual(expectedVersionsInMenu);
        },

        // Toolbar context
        pageVersionToolbarContextIsNotDisplayed: function () {
            expect(
                browser.isAbsent(componentObject.elements.getPageVersionToolbarContextSelector())
            ).toBe(true, 'Expected toolbar context to be absent');
        },
        toolbarContextHasPageVersionSelected: function (label) {
            expect(componentObject.elements.getPageVersionLabelInToolbarContext()).toEqual(label);
        },
        pageVersionToolbarContextPopoverIsDisplayed: function () {
            expect(
                browser.isPresent(componentObject.elements.getPageVersionDescriptionSelector())
            ).toBe(true, 'Expected toolbar context popover to be displayed.');
        },
        pageVersionToolbarContextPopoverIsNotDisplayed: function () {
            expect(
                browser.isAbsent(componentObject.elements.getPageVersionDescriptionSelector())
            ).toBe(true, 'Expected toolbar context popover to be absent.');
        },

        // Create Version Button
        createVersionButtonIsDisplayed: function () {
            expect(
                browser.isPresent(componentObject.elements.getCreateVersionMenuButtonSelector())
            ).toBe(true, "Expected 'Create Version' button to be present.");
        },
        createVersionButtonIsNotDisplayed: function () {
            expect(
                browser.isAbsent(componentObject.elements.getCreateVersionMenuButtonSelector())
            ).toBe(true, "Expected 'Create Version' button to be absent.");
        },

        // Rollback Version Button
        rollbackVersionButtonIsDisplayed: function () {
            expect(
                browser.isPresent(componentObject.elements.getRollbackVersionMenuButtonSelector())
            ).toBe(true, "Expected 'Rollback Version' button to be present.");
        },
        rollbackVersionButtonIsNotDisplayed: function () {
            expect(
                browser.isAbsent(componentObject.elements.getRollbackVersionMenuButtonSelector())
            ).toBe(true, "Expected 'Rollback Version' button to be absent.");
        },
        rollbackVersionSuccessAlertIsDisplayed: function () {
            browser.waitToBeDisplayed(componentObject.elements.getRollbackSuccessAlert());
        },

        // Delete Page Version
        deletedPageVersionIsNotAvailableInTheMenu: function (label) {
            componentObject.actions.searchVersion(label);
            expect(componentObject.elements.getVersionsCount()).toBe('0 Versions');
        },
        deletedPageVersionIsAvailableInTheMenu: function (label) {
            componentObject.actions.searchVersion(label);
            expect(componentObject.elements.getVersionsCount()).toBe('1 Versions');
        },

        // Edit Page Version
        editMenuButtonIsAbsent: function () {
            expect(componentObject.elements.getEditMenuItem()).toBeAbsent();
        },
        editMenuButtonIsDisplayed: function () {
            expect(componentObject.elements.getEditMenuItem()).toBeAbsent();
        },
        deleteMenuButtonIsAbsent: function () {
            expect(componentObject.elements.getDeleteMenuItem()).toBeAbsent();
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Utils
    // --------------------------------------------------------------------------------------------------
    componentObject.utils = {
        filterPageVersions: function (filter) {
            if (!filter || filter === '') {
                return PAGE_VERSIONS;
            }

            return PAGE_VERSIONS.filter(function (version) {
                return version.indexOf(filter) !== -1;
            });
        },
        getExpectedVersionsLoaded: function (items, pagesLoaded) {
            var totalAllowedItems = pagesLoaded * componentObject.constants.PAGE_SIZE;
            return items.slice(0, totalAllowedItems);
        },
        /**
         * The format for params object:
         * {
         *   read: true,
         *   change: false,
         *   create: true,
         *   remove: false
         * }
         * @param {*} params
         */
        setCMSVersionTypePermission: function (params) {
            var typePermissions = [
                {
                    key: 'read',
                    value: params.read.toString()
                },
                {
                    key: 'change',
                    value: params.change.toString()
                },
                {
                    key: 'create',
                    value: params.create.toString()
                },
                {
                    key: 'remove',
                    value: params.remove.toString()
                }
            ];
            browser.executeScript(
                'window.sessionStorage.setItem("cmsVersionTypePermissions", arguments[0])',
                JSON.stringify(typePermissions)
            );
        }
    };

    return componentObject;
})();
