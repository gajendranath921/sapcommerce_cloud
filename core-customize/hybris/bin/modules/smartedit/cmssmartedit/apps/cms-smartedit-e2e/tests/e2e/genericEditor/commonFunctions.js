/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
var yTabComponent = e2e.componentObjects.yTab;
var select = e2e.componentObjects.Select;
const CSS_PREIFX = '[id="media"] se-tab[tab-id="';
const ID_POSTFIX = '-shortstring';

selectLocalizedTab = function (language, qualifier) {
    return browser.click(element(by.css(`#${qualifier} .se-tabset__tab[tab-id="${language}"]`)));
};

switchToIframeForRichTextAndAddContent = function (iframeId, content) {
    browser.waitUntil(EC.presenceOf(element(by.css(iframeId))));
    browser.switchTo().frame(element(by.css(iframeId)).getWebElement(''));
    browser.driver.findElement(by.tagName('body')).sendKeys(content);
};

switchToIframeForRichTextAndValidateContent = function (iframeId, content) {
    browser.waitUntil(EC.presenceOf(element(by.css(iframeId))));
    browser.switchTo().frame(element(by.css(iframeId)).getWebElement(''));
    expect(browser.driver.findElement(by.tagName('body')).getText()).toEqual(content);
};

getValidationErrorElements = function (qualifier) {
    return element.all(
        by.css(
            '[id="' +
                qualifier +
                '"] se-generic-editor-field-messages span.se-help-block--has-error'
        )
    );
};

getValidationErrorElementByLanguage = function (qualifier, language) {
    return element(
        by.css(
            '[tab-id="' +
                language +
                '"] [validation-id="' +
                qualifier +
                '"] se-generic-editor-field-messages .se-help-block--has-error'
        )
    );
};

selectMediaTab = function (language) {
    return browser.click(
        by.xpath("//*[@id='media']//li[@tab-id='" + language + "']"),
        'could not find tab for language: ' + language
    );
};

getMediaTabByTabId = function (id) {
    return element(by.css(`#media.ySEGenericEditorFieldStructure se-tab[tab-id="${id}"]`));
};

addMedia = function (language, searchKey) {
    return selectMediaTab(language).then(function () {
        return browser
            .click(
                by.xpath("//*[@tab-id='" + language + "']//*[text()='Search...']"),
                "could not find 'Search...' placeholder for language tab: " + language
            )
            .then(function () {
                return browser
                    .sendKeys(
                        getMediaTabByTabId(language).element(
                            select.locators.getSearchInput('media')
                        ),
                        searchKey,
                        'could not enter mask in media search for language tab: ' + language
                    )
                    .then(function () {
                        var option = getMediaTabByTabId(language).element(
                            select.locators.getOptionByText('media', searchKey)
                        );

                        return browser.waitForPresence(option).then(function () {
                            return browser.waitUntil(function () {
                                return option.click().then(function () {
                                    return true;
                                });
                            }, 'could not click on media selection for language tab: ' + language);
                        });
                    });
            });
    });
};

getMediaElement = function (language) {
    return element(by.css(`${CSS_PREIFX}${language}"] .se-media-preview__image-thumbnail`));
};

getMediaElementWrapper = function (language) {
    return element(by.css(`${CSS_PREIFX}${language}"] .se-media-selector__left-section`));
};

assertOnMediaInTab = function (language, expectedContent) {
    return selectMediaTab(language).then(function () {
        return browser.click(getMediaElementInfoAncor(language)).then(function () {
            return getMediaElementCode()
                .getText()
                .then(function (content) {
                    return content.indexOf(expectedContent) > -1;
                });
        });
    });
};

getMediaElementCode = function () {
    return element(by.css('.se-media-advanced-info-row__description'));
};

getMediaElementInfoAncor = function (language) {
    return element(by.css(`${CSS_PREIFX}${language}"] .se-media-advanced-info`));
};

switchToDefaultContent = function () {
    browser.driver.switchTo().defaultContent();
};

clickSubmit = function () {
    return browser.click(by.id('submit'));
};

/**
 * Simplifies entering value to localized short string component
 * @param {String} qualifier
 * @param {String} text
 * @param {String} language
 */
enterLocalizedShortStringText = function (qualifier, text, language) {
    return yTabComponent.elements
        .getTabById(language || 'en')
        .element(by.id(qualifier + ID_POSTFIX))
        .sendKeys(text);
};

/**
 * Simplifies extracting localized short string element
 * @param {String} qualifier
 * @param {String} language
 * @returns {*|string|!webdriver.promise.Promise.<?string>}
 */
getLocalizedShortString = function (qualifier, language) {
    return yTabComponent.elements
        .getTabById(language || 'en')
        .element(by.id(qualifier + ID_POSTFIX));
};

/**
 * Simplifies entering value to short string component
 * @param {String} qualifier
 * @param {String} text
 */
enterShortStringText = function (qualifier, text) {
    element(by.id(qualifier + ID_POSTFIX)).sendKeys(text);
};

/**
 * Simplifies extracting short string element
 * @param {String} qualifier
 * @returns {!webdriver.promise.Promise.<?string>}
 */
getShortString = function (qualifier) {
    return element(by.id(qualifier + ID_POSTFIX));
};

/**
 * productMocks return only one product catalog or more than one product catalog depending the value of the sessionStorage.getItem('returnOneCatalog')
 */
setReturnOneCatalog = function (returnOneCatalog) {
    return browser.executeScript(
        'window.sessionStorage.setItem("returnOneCatalog", arguments[0])',
        returnOneCatalog
    );
};
