/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const XPATH_PREFIX = "//div/span[.='";
module.exports = {
    addChildButtonLabel: 'child',
    addSiblingButtonLabel: 'sibling',
    removeButtonClass: 'glyphicon-remove',
    collapseButtonClass: 'sap-icon--slim-arrow-down',
    expandButtonClass: 'sap-icon--slim-arrow-right',

    _getNode: function (nodeLabel) {
        return by.xpath("//span[.='" + nodeLabel + "']/preceding-sibling::a");
    },
    _getNodeNames: function (array, rows, index) {
        var deferred = protractor.promise.defer();
        if (rows[index]) {
            rows[index].getText().then(
                function (value) {
                    array.push(value);
                    if (index < rows.length - 1) {
                        this._getNodeNames(array, rows, index + 1).then(function (arr) {
                            deferred.fulfill(arr);
                        });
                    } else {
                        deferred.fulfill(array);
                    }
                }.bind(this)
            );
        } else {
            deferred.fulfill(array);
        }
        return deferred.promise;
    },
    _getNodeHandle: function (nodeLabel) {
        return by.xpath(`${XPATH_PREFIX}${nodeLabel}']/ancestor::div[2]`);
    },
    clickOnLinkHavingClass: function (nodeLabel, cssClass) {
        if (cssClass === this.collapseButtonClass || cssClass === this.expandButtonClass) {
            return browser.click(
                by.xpath(
                    `${XPATH_PREFIX}${nodeLabel}']/ancestor::div[2]/div/a/span[contains(@class, '${cssClass}')]`
                )
            );
        } else {
            return browser.click(
                by.xpath(
                    `${XPATH_PREFIX}${nodeLabel}']/ancestor::div[2]/a/span[contains(@class, '${cssClass}')]`
                )
            );
        }
    },
    clickOnLinkHavingContent: function (nodeLabel, anchorLabel) {
        return browser.click(
            by.xpath(
                `${XPATH_PREFIX}${nodeLabel}']/ancestor::div[2]/a[contains(.,'${anchorLabel}')]`
            )
        );
    },
    getAndWaitForWholeApp: function () {
        browser.get('smartedit-e2e/generated/e2e/tree/#!/tree');
        browser.waitUntil(
            protractor.ExpectedConditions.elementToBeClickable(element(by.css('ytree'))),
            'could not find a single yTree in the page'
        );
        browser.waitForAngular();
    },
    clickNodeByText: function (nodeLabel) {
        return browser.click(this._getNode(nodeLabel));
    },
    addSibling: function (nodeLabel) {
        return this.clickOnLinkHavingContent(nodeLabel, this.addSiblingButtonLabel);
    },
    addChild: function (nodeLabel) {
        return this.clickOnLinkHavingContent(nodeLabel, this.addChildButtonLabel);
    },
    remove: function (nodeLabel) {
        return this.clickOnLinkHavingClass(nodeLabel, this.removeButtonClass);
    },
    expand: function (nodeLabel) {
        return this.clickOnLinkHavingClass(nodeLabel, this.expandButtonClass);
    },
    collapse: function (nodeLabel) {
        return this.clickOnLinkHavingClass(nodeLabel, this.collapseButtonClass);
    },
    getChildrenNames: function (nodeLabel) {
        var deferred = protractor.promise.defer();
        var selector;
        if (nodeLabel) {
            selector = by.xpath(
                `${XPATH_PREFIX}${nodeLabel}']/ancestor::li[1]/ol/li/div/div[2]/span`
            );
        } else {
            selector = by.xpath('//ytree/div/div/div/ol/li/div/div[2]/span');
        }
        element.all(selector).then(
            function (children) {
                this._getNodeNames([], children, 0).then(function (array) {
                    deferred.fulfill(array);
                });
            }.bind(this)
        );
        return deferred.promise;
    },
    getChildrenCount: function (nodeLabel) {
        return this.getChildrenNames(nodeLabel).then(function (children) {
            return children.length;
        });
    },
    getNthChild: function (nodeLabel, index) {
        return this.getChildrenNames(nodeLabel).then(function (children) {
            return children[index];
        });
    },
    startDraggingNode: function (nodeLabel) {
        var node = browser.findElement(this._getNodeHandle(nodeLabel));
        return browser
            .actions()
            .mouseMove(node)
            .mouseDown()
            .perform()
            .then(
                function () {
                    return browser.sleep(this.dragDelay);
                }.bind(this)
            );
    },
    moveMouseToNode: function (nodeLabel) {
        var node = browser.findElement(this._getNodeHandle(nodeLabel));
        return browser.actions().mouseMove(node).perform();
    },
    moveMouseToNodeOffsetUp: function (nodeLabel) {
        return this.moveMouseToNode(nodeLabel).then(function () {
            return browser
                .actions()
                .mouseMove({
                    x: 1,
                    y: -20
                })
                .perform();
        });
    },
    moveMouseToNodeOffsetDown: function (nodeLabel) {
        return this.moveMouseToNode(nodeLabel).then(function () {
            return browser
                .actions()
                .mouseMove({
                    x: 1,
                    y: 10
                })
                .perform();
        });
    },
    mouseUp: function () {
        return browser.actions().mouseUp().perform();
    },
    dragDelay: 200,
    modal: function () {
        return element(by.css('.se-confirmation-dialog'));
    },
    modalDescription: function () {
        return this.modal().element(by.id('confirmationModalDescription'));
    },
    confirmModal: function () {
        return browser.click(element(by.css('#confirmOk')));
    },
    cancelModal: function () {
        return browser.click(element(by.css('#confirmCancel')));
    }
};
