/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
module.exports = (function () {
    var pageRestrictionsEditor = {};

    pageRestrictionsEditor.constants = {
        CONTENT_PAGE_TYPE: 'ContentPage',
        CATEGORY_PAGE_TYPE: 'CategoryPage'
    };

    pageRestrictionsEditor.elements = {
        getPageRestrictionsEditor: function () {
            return element(by.tagName('se-restrictions-editor'));
        },
        saveButton: function () {
            return element(by.css('#test-save-button-id'));
        },
        saveResultMessage: function () {
            return element(by.css('#save-result-msg'));
        }
    };

    pageRestrictionsEditor.actions = {
        navigateToPage: function (pageType, includeExistingRestrictions, dirname) {
            return browser.bootstrap(dirname).then(function () {
                if (!pageType) {
                    pageType = pageRestrictionsEditor.constants.CONTENT_PAGE_TYPE;
                }
                browser.executeScript(
                    'window.sessionStorage.setItem("returnErrors", arguments[0])',
                    false
                );
                browser.executeScript(
                    'window.sessionStorage.setItem("pageType", arguments[0])',
                    pageType
                );
                browser.executeScript(
                    'window.sessionStorage.setItem("existingRestrictions", arguments[0])',
                    includeExistingRestrictions
                );
            });
        },
        clickSave: function () {
            return browser.click(pageRestrictionsEditor.elements.saveButton());
        },
        clickSaveAndPropagateErrors: function () {
            browser.executeScript(
                'window.sessionStorage.setItem("returnErrors", arguments[0])',
                true
            );
            return browser.click(pageRestrictionsEditor.elements.saveButton());
        }
    };

    pageRestrictionsEditor.assertions = {
        saveNotExecuted: function () {
            expect(pageRestrictionsEditor.elements.saveResultMessage().getText()).toBe(
                'Save cannot be executed. Editor not dirty.'
            );
        },
        saveExecuted: function () {
            expect(pageRestrictionsEditor.elements.saveResultMessage().getText()).toBe(
                'Save executed.'
            );
        }
    };

    return pageRestrictionsEditor;
})();
