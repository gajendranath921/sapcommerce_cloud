/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
var storefront;
if (typeof require !== 'undefined') {
    storefront = require('./storefrontComponentObject');
}

module.exports = (function () {
    var componentObject = {};

    componentObject.constants = {
        HIDDEN_COMPONENT_1: 'hiddenComponent1',
        HIDDEN_COMPONENT_2: 'hiddenComponent2'
    };

    componentObject.elements = {
        // -- HIDDEN COMPONENTS (VISIBILITY BUTTON) --
        visibilityButtonBySlotId: function (slotId) {
            return element(by.id('slot-visibility-button-' + slotId));
        },
        visibilityDropdownBySlotId: function (slotId) {
            return element(by.id('slot-visibility-list-' + slotId));
        },
        visibilityListBySlotId: function (slotId) {
            return this.visibilityDropdownBySlotId(slotId).all(
                by.css('.se-slot-visiblity-component__content')
            );
        },
        hiddenComponentInSlot: function (slotId, componentId) {
            return this.visibilityDropdownBySlotId(slotId).element(
                by.css('se-slot-visibility-component[data-component-id="' + componentId + '"]')
            );
        },
        hiddenComponentMenuButton: function (slotId, componentId) {
            return this.hiddenComponentInSlot(slotId, componentId).element(
                by.css('se-hidden-component-menu .se-hidden-component-menu__popup-anchor')
            );
        },
        hiddenComponentMenu: function () {
            return element(by.css('.se-hidden-component-menu'));
        },
        hiddenComponentMenuItem: function (menuItemLabel) {
            return this.hiddenComponentMenu().element(
                by.cssContainingText('.se-hidden-component-menu__item-link', menuItemLabel)
            );
        },
        hiddenComponentSharedIndicator: function (slotId, componentId) {
            return this.hiddenComponentInSlot(slotId, componentId).element(
                by.css('.se-slot-visibility__icon--shared')
            );
        },

        // -- SHARED SLOT BUTTON --
        sharedSlotButtonBySlotId: function (slotId) {
            return element(by.id('sharedSlotButton-' + slotId));
        },
        sharedSlotButtonMessage: function () {
            return browser
                .findElement(by.css('.se-shared-slot__description'))
                .getText()
                .then(function (text) {
                    return text;
                });
        },
        sharedSlotButtonMenu: function () {
            return browser.findElement(by.css('.shared-slot-button-template__menu'));
        },
        replaceSlotLink: function () {
            return element(by.css('.se-shared-slot__link.replace-slot-link'));
        },
        replaceComponentsInSlotOption: function () {
            return element(by.id('components-in-slot-options'));
        },
        replaceSlotTypeOption: function () {
            return element(by.id('slot-replacement-type-options'));
        },
        replaceSlotCloneOption: function () {
            return element(by.css('#components-in-slot-options input#clone-all'));
        },
        // -- UNSHARED SLOT BUTTON --
        revertSlotLink: function () {
            return element(by.css('.se-shared-slot__link.revert-slot-link'));
        },
        unsharedSlotButtonBySlotId: function (slotId) {
            return element(by.id('slot-unshared-button-' + slotId));
        },
        unsharedSlotButtonMessage: function () {
            browser.waitUntil(function () {
                return element
                    .all(by.css('.se-shared-slot__link-description__unshared'))
                    .then(function (popovers) {
                        return popovers.length === 1;
                    });
            }, 'no popovers are available');

            return browser
                .findElement(by.css('.se-shared-slot__link-description__unshared'))
                .getAttribute('innerHTML')
                .then(function (innerHTML) {
                    return innerHTML.replace(' class="ng-scope"', '');
                });
        },

        // -- SYNC BUTTON --
        syncButtonBySlotId: function (slotId) {
            return element(by.id('slot-sync-button-' + slotId));
        },
        syncyDropdownBySlotId: function (slotId) {
            return element(by.id('slot-sync-list-' + slotId));
        },
        syncButtonStatusBySlotId: function (slotId) {
            browser.waitForPresence(
                by.css("[data-smartedit-component-id='" + slotId + "'] slot-sync-button"),
                'cannot find slot sync icon'
            );
            return element(by.css('#slot-sync-button-' + slotId + ' .se-slot-sync__btn-status'));
        },

        // -- SYNC BUTTON POPOVER --
        popover: function () {
            return element(by.css('.se-popover--inner-content'));
        }
    };

    componentObject.actions = {
        clickOnSharedSlotBySlotId: function (slotId) {
            return browser.click(componentObject.elements.sharedSlotButtonBySlotId(slotId));
        },
        hoverOverSlotSyncButtonBySlotId: function (slotId) {
            return browser
                .actions()
                .mouseMove(componentObject.elements.syncButtonBySlotId(slotId))
                .perform();
        },
        openSharedSlotButtonByButtonId: function (slotId) {
            return browser.click(componentObject.elements.sharedSlotButtonBySlotId(slotId));
        },
        clickOnReplaceSlotLink: function () {
            var replaceLink = componentObject.elements.replaceSlotLink();
            browser.waitForPresence(replaceLink, 'Expected replace slot link to be present');
            return browser.click(replaceLink);
        },
        clickOnRevertSlotLink: function () {
            var revertLink = componentObject.elements.revertSlotLink();
            browser.waitForPresence(revertLink, 'Expected revert slot link to be present');
            return browser.click(revertLink);
        },
        clickComponentsInSlotCloneOption: function () {
            return browser.click(componentObject.elements.replaceSlotCloneOption());
        },
        // -- HIDDEN COMPONENTS (VISIBILITY BUTTON) --
        clickOnSlotVisibilityButton: function (slotId) {
            return browser.click(componentObject.elements.visibilityButtonBySlotId(slotId));
        },
        openHiddenComponentsList: function (slotId) {
            return storefront.actions.moveToComponent(slotId).then(
                function () {
                    return this.clickOnSlotVisibilityButton(slotId);
                }.bind(this)
            );
        },
        openHiddenComponentMenu: function (slotId, componentId) {
            return browser.click(
                componentObject.elements.hiddenComponentMenuButton(slotId, componentId)
            );
        },
        openFirstHiddenComponentMenu: function (slotId) {
            return this.openHiddenComponentMenu(
                slotId,
                componentObject.constants.HIDDEN_COMPONENT_1
            );
        },
        clickHiddenComponentMenuItemByLabel: function (menuItemLabel) {
            componentObject.assertions.menuItemIsDisplayedByLabel(menuItemLabel);
            return browser.click(componentObject.elements.hiddenComponentMenuItem(menuItemLabel));
        },

        // -- SHARED/UNSHARED --
        clickOnSlotUnsharedButtonBySlotId: function (slotId) {
            return browser.click(componentObject.elements.unsharedSlotButtonBySlotId(slotId));
        },
        hoverSlot: function (slotId) {
            // There was an issue with some tests being flaky in smaller screens; Protractor
            // complained that it could not click the element in that position. Apparently,
            // this was caused by the slot being outside the viewport. To avoid this issue,
            // instead of just moving the mouse, this method scrolls the component into view
            // and then just moves the mouse a little to trigger the slot contextual menu.
            return storefront.actions.scrollComponentIntoView(slotId).then(function () {
                return browser
                    .actions()
                    .mouseMove({
                        x: 1,
                        y: 1
                    })
                    .perform();
            });
        },

        // -- SLOT SYNC PANEL --
        openSlotSyncPanel: function (slotId) {
            return storefront.actions.moveToComponent(slotId).then(function () {
                return browser.click(componentObject.elements.syncButtonBySlotId(slotId));
            });
        }
    };

    componentObject.assertions = {
        menuItemIsDisplayedByLabel: function (menuItemLabel) {
            expect(
                componentObject.elements.hiddenComponentMenuItem(menuItemLabel),
                'Expected menu item with label ' + menuItemLabel + ' to be displayed'
            );
        },

        // Sync
        disabledSyncButtonShowsPopover: function () {
            expect(componentObject.elements.popover().getText()).toBe(
                'This slot can be synced from the page level'
            );
        },
        syncButtonIsDisabled: function (slotId) {
            // In slotSyncButtonTemplate.html we actually don't disable the button but move the hover component over it.
            // That's why we can not check the disabled attribute.
            expect(
                componentObject.elements.syncButtonBySlotId(slotId).getAttribute('class')
            ).toContain('se-slot-ctx-menu__dropdown-toggle--disabled');
        },
        syncButtonIsAbsent: function (slotId) {
            expect(browser.isAbsent(componentObject.elements.syncButtonBySlotId(slotId))).toBe(
                true
            );
        },
        syncButtonIsPresent: function (slotId) {
            expect(browser.isPresent(componentObject.elements.syncButtonBySlotId(slotId))).toBe(
                true
            );
        },
        syncButtonStatusIsPresentBySlotId: function (slotId) {
            expect(componentObject.elements.syncButtonStatusBySlotId(slotId).isPresent()).toBe(
                true
            );
        },

        // Share-Unshare
        assertThatSlotShareButtonIsNotPresent: function (slotId) {
            componentObject.actions.hoverSlot(slotId);
            var button = componentObject.elements.sharedSlotButtonBySlotId(slotId);

            browser.waitForAbsence(button, 'Expected shared slot button not to be visible');
        },
        assertThatSlotUnsharedButtonIsPresent: function (slotId) {
            componentObject.actions.hoverSlot(slotId);
            var button = componentObject.elements.unsharedSlotButtonBySlotId(slotId);

            browser.waitForPresence(button, 'Expected unshared slot button to be present');
            expect(button.isDisplayed()).toBe(
                true,
                'Expected unshared slot button to be displayed'
            );
        },
        assertThatSlotUnsharedButtonIsNotPresent: function (slotId) {
            storefront.actions.moveToComponent(slotId);
            var button = componentObject.elements.unsharedSlotButtonBySlotId(slotId);
            expect(button).toBeAbsent();
        },
        assertThatSlotSharedButtonOverrideLinkToBeAbsent: function () {
            var replaceLink = componentObject.elements.replaceSlotLink();
            browser.waitForAbsence(replaceLink);
        },
        assertThatSlotReplaceComponentInSlotOptionIsPresent: function () {
            browser.waitForPresence(
                componentObject.elements.replaceComponentsInSlotOption(),
                'Expected Components In Slot option to be present'
            );
        },
        assertThatSlotReplaceSlotTypeOption: function (shouldBePresent) {
            if (shouldBePresent) {
                browser.waitForPresence(
                    componentObject.elements.replaceSlotTypeOption(),
                    'Expected Slot Replacement Type option to be present'
                );
            } else {
                browser.waitForAbsence(
                    componentObject.elements.replaceSlotTypeOption(),
                    'Expected Slot Replacement Type option not to be present'
                );
            }
        },
        // Hidden Components
        assertHiddenComponentIsShared: function (slotId, componentId) {
            expect(
                componentObject.elements
                    .hiddenComponentSharedIndicator(slotId, componentId)
                    .isDisplayed()
            ).toBe(true);
        },
        assertHiddenComponentIsNotShared: function (slotId, componentId) {
            expect(
                browser.isAbsent(
                    componentObject.elements.hiddenComponentSharedIndicator(slotId, componentId)
                )
            ).toBe(true);
        }
    };

    return componentObject;
})();
