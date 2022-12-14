/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { browser, by } from 'protractor';
import { AlertsComponentObject } from '../../utils/components/AlertsComponentObject';
import { ExperienceSelectorObject } from '../../utils/components/ExperienceSelector';

describe('Experience Selector - ', () => {
    const ELECTRONICS_CONTENT_CATALOG = {
        CATALOGS: {
            ONLINE: 'Electronics Content Catalog - Online',
            STAGED: 'Electronics Content Catalog - Staged'
        }
    };

    const APPAREL_SITE = {
        CATALOGS: {
            ONLINE: 'Apparel UK Content Catalog - Online',
            STAGED: 'Apparel UK Content Catalog - Staged'
        },
        LANGUAGES: {
            ENGLISH: 'English',
            FRENCH: 'French'
        }
    };

    const APPAREL_PRODUCT_CATALOG_CLOTHING = {
        ID: 'apparel-ukProductCatalog-clothing',
        VERSIONS: {
            ONLINE: 'Online',
            STAGED: 'Staged'
        }
    };

    const APPAREL_PRODUCT_CATALOG_SHOES = {
        ID: 'apparel-ukProductCatalog-shoes',
        VERSIONS: {
            ONLINE: 'Online',
            STAGED_1: 'Staged-1',
            STAGED_2: 'Staged-2'
        }
    };

    const PLACEHOLDER_SELECT_A_DATE_AND_TIME = 'Select a Date and Time';
    const OPTION_PRODUCT_CATALOG_STAGE =
        'Clothing Product Catalog (Staged), Shoes Product Catalog (Staged-2)';
    beforeEach(async () => {
        await browser.get('smartedit-e2e/generated/e2e/experienceSelector/#!/ng/storefront');
        await browser.waitForWholeAppToBeReady();
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector button THEN I expect to see the Experience Selector", async () => {
        // WHEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // THEN
        expect(await ExperienceSelectorObject.Elements.activeSite.label().getText()).toBe(
            'ActiveSite'
        );
        expect(await ExperienceSelectorObject.Elements.activeSite.text().getText()).toBe(
            'Apparels'
        );
        expect(await ExperienceSelectorObject.Elements.catalog.label().getText()).toBe('Catalog');
        expect(await ExperienceSelectorObject.Elements.dateAndTime.label().getText()).toBe(
            'Date And Time'
        );
        expect(await (await ExperienceSelectorObject.Elements.language.label()).getText()).toBe(
            'Language'
        );
        expect(await ExperienceSelectorObject.Elements.productCatalogs.label().getText()).toBe(
            'PRODUCT CATALOGS'
        );

        expect(await ExperienceSelectorObject.Elements.buttons.ok().getText()).toBe('APPLY');
        expect(await ExperienceSelectorObject.Elements.buttons.cancel().getText()).toBe('Cancel');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector for a site that has a single product catalog THEN I expect to see the currently selected experience in the Experience Selector", async () => {
        // WHEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // THEN
        expect(await ExperienceSelectorObject.Elements.catalog.selectedOption().getText()).toBe(
            APPAREL_SITE.CATALOGS.STAGED
        );
        expect(
            await (await ExperienceSelectorObject.Elements.language.selectedOption()).getText()
        ).toBe('English');
        expect(
            await (await ExperienceSelectorObject.Elements.dateAndTime.field()).getAttribute(
                'placeholder'
            )
        ).toBe(PLACEHOLDER_SELECT_A_DATE_AND_TIME);
        expect(
            await ExperienceSelectorObject.Elements.multiProductCatalogVersionsSelector.selectedOptions()
        ).toBe('Clothing Product Catalog (Online), Shoes Product Catalog (Online)');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector for a site that has multiple product catalogs THEN I expect to see the currently selected experience in the Experience Selector", async () => {
        // WHEN
        await ExperienceSelectorObject.Actions.switchToCatalogVersion(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );

        await ExperienceSelectorObject.Actions.productCatalogs.openMultiProductCatalogVersionsSelectorWidget();

        await ExperienceSelectorObject.Actions.productCatalogs.selectOptionFromMultiProductCatalogVersionsSelectorWidget(
            APPAREL_PRODUCT_CATALOG_CLOTHING.ID,
            APPAREL_PRODUCT_CATALOG_CLOTHING.VERSIONS.STAGED
        );
        await ExperienceSelectorObject.Actions.productCatalogs.selectOptionFromMultiProductCatalogVersionsSelectorWidget(
            APPAREL_PRODUCT_CATALOG_SHOES.ID,
            APPAREL_PRODUCT_CATALOG_SHOES.VERSIONS.STAGED_2
        );

        await ExperienceSelectorObject.Actions.productCatalogs.clickModalWindowDone();

        // THEN
        const catalogSelectedOption = ExperienceSelectorObject.Elements.catalog.selectedOption();
        await browser.waitForPresence(catalogSelectedOption, 'Catalog Selected Option not present');
        expect(await catalogSelectedOption.getText()).toBe(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );
        expect(
            await (await ExperienceSelectorObject.Elements.language.selectedOption()).getText()
        ).toBe('English');
        expect(
            await (await ExperienceSelectorObject.Elements.dateAndTime.field()).getAttribute(
                'placeholder'
            )
        ).toBe(PLACEHOLDER_SELECT_A_DATE_AND_TIME);
        expect(
            await ExperienceSelectorObject.Elements.multiProductCatalogVersionsSelector.selectedOptions()
        ).toBe(OPTION_PRODUCT_CATALOG_STAGE);
    });

    it("GIVEN I'm in the experience selector WHEN I click on the catalog selector dropdown THEN I expect to see all catalog/catalog versions combinations", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // WHEN
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();

        // THEN
        await ExperienceSelectorObject.Assertions.catalog.assertNumberOfOptions(4);
        await ExperienceSelectorObject.Assertions.catalog.assertOptionText(
            0,
            APPAREL_SITE.CATALOGS.ONLINE
        );
        await ExperienceSelectorObject.Assertions.catalog.assertOptionText(
            1,
            APPAREL_SITE.CATALOGS.STAGED
        );
        await ExperienceSelectorObject.Assertions.catalog.assertOptionText(
            2,
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );
        await ExperienceSelectorObject.Assertions.catalog.assertOptionText(
            3,
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.STAGED
        );
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog THEN I expect to see the apply button enabled", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // WHEN
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);

        // THEN
        expect(
            await ExperienceSelectorObject.Elements.buttons.ok().getAttribute('disabled')
        ).toBeFalsy();
    });

    it(
        "GIVEN I'm in the experience selector WHEN I select electronics catalog belonging to the apparel site THEN I expect to" +
            ' see the language dropdown populated with the apparel sites languages',
        async () => {
            // GIVEN
            await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

            // WHEN
            await ExperienceSelectorObject.Actions.catalog.selectDropdown();
            await ExperienceSelectorObject.Actions.catalog.selectOption(
                ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
            );
            await ExperienceSelectorObject.Actions.language.selectDropdown();

            // THEN
            await ExperienceSelectorObject.Assertions.language.assertNumberOfOptions(2);
            await ExperienceSelectorObject.Assertions.language.assertOptionText(0, 'English');
            await ExperienceSelectorObject.Assertions.language.assertOptionText(1, 'French');
        }
    );

    it(
        "GIVEN I'm in the experience selector WHEN I select apparel catalog belonging to the apparel site THEN I expect to see" +
            ' the language dropdown populated with the apprel sites languages',
        async () => {
            // GIVEN
            await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

            // WHEN
            await ExperienceSelectorObject.Actions.catalog.selectDropdown();
            await ExperienceSelectorObject.Actions.catalog.selectOption(
                APPAREL_SITE.CATALOGS.ONLINE
            );
            await ExperienceSelectorObject.Actions.language.selectDropdown();

            await ExperienceSelectorObject.Assertions.language.assertNumberOfOptions(2);
            await ExperienceSelectorObject.Assertions.language.assertOptionText(0, 'English');
            await ExperienceSelectorObject.Assertions.language.assertOptionText(1, 'French');
        }
    );

    it(
        "GIVEN I'm in the experience selector WHEN I click the apply button AND the REST call to the preview service" +
            ' succeeds THEN I expect the smartEdit application with the new preview ticket',
        async () => {
            // GIVEN
            await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
            await ExperienceSelectorObject.Actions.catalog.selectDropdown();
            await ExperienceSelectorObject.Actions.catalog.selectOption(
                APPAREL_SITE.CATALOGS.ONLINE
            );
            await ExperienceSelectorObject.Actions.language.selectDropdown();
            await ExperienceSelectorObject.Actions.language.selectOption(
                APPAREL_SITE.LANGUAGES.ENGLISH
            );

            // WHEN
            await ExperienceSelectorObject.Actions.widget.submit();
            await browser.waitForWholeAppToBeReady();

            // THEN
            const expectedUriPostfix =
                '/apps/smartedit-e2e/generated/pages/storefront.html?cmsTicketId=validTicketId';
            expect(
                await ExperienceSelectorObject.Elements.page.iframe().getAttribute('src')
            ).toContain(expectedUriPostfix);
        }
    );

    // TODO this should be part of a unit test
    it("GIVEN I'm in the experience selector WHEN I click the apply button AND the REST call to the preview service fails due to an invalid catalog and catalog version THEN I expect to see an error displayed", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );
        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();

        // THEN
        await AlertsComponentObject.Assertions.assertTotalNumberOfAlerts(1);
        await AlertsComponentObject.Assertions.assertAlertIsOfTypeByIndex(0, 'error');
    });

    it("GIVEN I'm in the experience selector AND I click on the apply button to update the experience with the one I chose THEN it should update the experience widget text", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();

        // THEN
        const VALID_EXPERIENCE_WIDGET_TEXT = 'Apparel UK Content Catalog - Online | French';
        expect(await ExperienceSelectorObject.Elements.widget.text()).toContain(
            VALID_EXPERIENCE_WIDGET_TEXT
        );
    });

    it("GIVEN I'm in the experience selector AND I select a date and time using the date-time picker WHEN I click the apply button THEN it should update the experience widget text", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);

        await browser.click(ExperienceSelectorObject.Elements.dateAndTime.button());
        await ExperienceSelectorObject.Actions.selectExpectedDate();
        await browser.click(ExperienceSelectorObject.Elements.dateAndTime.button());

        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();

        // THEN
        const VALID_EXPERIENCE_WIDGET_TEXT =
            'Apparel UK Content Catalog - Online | French | 1/1/16 1:00 PM | Clothing Product Catalog (Online) | Shoes Product Catalog (Online)';
        expect(await ExperienceSelectorObject.Elements.widget.text()).toBe(
            VALID_EXPERIENCE_WIDGET_TEXT
        );
    });

    // TO BE DISCUSSED
    it("GIVEN I'm in the experience selector WHEN I click outside the experience selector in the SmartEdit container THEN the experience selector is closed and reset", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // WHEN
        await ExperienceSelectorObject.Actions.clickInApplication();

        // THEN
        await browser.waitForAbsence(ExperienceSelectorObject.Elements.catalog.label());
    });

    it("GIVEN I'm in the experience selector WHEN I click outside the experience selector in the SmartEdit application THEN the experience selector is closed and reset", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        // WHEN
        await ExperienceSelectorObject.Actions.clickInIframe();

        // THEN
        await browser.waitForAbsence(ExperienceSelectorObject.Elements.catalog.label());
    });

    it('GIVEN I have selected an experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the newly selected experience', async () => {
        const timeStamp = '1/1/16 12:01 AM';
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.STAGED
        );

        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(
            APPAREL_SITE.LANGUAGES.ENGLISH
        );

        await browser.click(await ExperienceSelectorObject.Elements.dateAndTime.field());
        await ExperienceSelectorObject.Actions.calendar.setDate(timeStamp);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        // THEN
        expect(await ExperienceSelectorObject.Elements.catalog.selectedOption().getText()).toBe(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.STAGED
        );
        expect(
            await (await ExperienceSelectorObject.Elements.language.selectedOption()).getText()
        ).toBe('English');

        expect(
            await (await ExperienceSelectorObject.Elements.dateAndTime.field()).getAttribute(
                'value'
            )
        ).toBe(timeStamp);
    });

    it('GIVEN I have selected an experience without a time WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the newly selected experience', async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );

        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(
            APPAREL_SITE.LANGUAGES.ENGLISH
        );

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // THEN
        expect(await ExperienceSelectorObject.Elements.catalog.selectedOption().getText()).toBe(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );
        expect(
            await (await ExperienceSelectorObject.Elements.language.selectedOption()).getText()
        ).toBe('English');
        expect(
            await (await ExperienceSelectorObject.Elements.dateAndTime.field()).getAttribute(
                'placeholder'
            )
        ).toBe(PLACEHOLDER_SELECT_A_DATE_AND_TIME);
    });

    it("GIVEN I'm in the experience selector AND I've changed the values in the editor fields WHEN I click cancel AND I re-open the experience selector THEN I expect to see the currently selected experience", async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.STAGED
        );

        await browser.click(await ExperienceSelectorObject.Elements.dateAndTime.field());
        await ExperienceSelectorObject.Actions.calendar.setDate('1/1/16 12:02 AM');

        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.cancel();
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // THEN
        expect(await ExperienceSelectorObject.Elements.catalog.selectedOption().getText()).toBe(
            APPAREL_SITE.CATALOGS.STAGED
        );
        expect(
            await (await ExperienceSelectorObject.Elements.language.selectedOption()).getText()
        ).toBe('English');
        expect(
            await (await ExperienceSelectorObject.Elements.dateAndTime.field()).getAttribute(
                'placeholder'
            )
        ).toBe(PLACEHOLDER_SELECT_A_DATE_AND_TIME);
    });

    it('GIVEN Im in a site that has multiple product catalogs WHEN I change the target versions and click apply button THEN I expect to see the currently selected experience', async () => {
        // GIVEN
        await ExperienceSelectorObject.Actions.switchToCatalogVersion(APPAREL_SITE.CATALOGS.ONLINE);

        // WHEN
        await ExperienceSelectorObject.Actions.productCatalogs.openMultiProductCatalogVersionsSelectorWidget();

        await ExperienceSelectorObject.Actions.productCatalogs.selectOptionFromMultiProductCatalogVersionsSelectorWidget(
            APPAREL_PRODUCT_CATALOG_CLOTHING.ID,
            APPAREL_PRODUCT_CATALOG_CLOTHING.VERSIONS.STAGED
        );
        await ExperienceSelectorObject.Actions.productCatalogs.selectOptionFromMultiProductCatalogVersionsSelectorWidget(
            APPAREL_PRODUCT_CATALOG_SHOES.ID,
            APPAREL_PRODUCT_CATALOG_SHOES.VERSIONS.STAGED_2
        );

        await ExperienceSelectorObject.Actions.productCatalogs.clickModalWindowDone();

        // THEN
        expect(
            await ExperienceSelectorObject.Elements.multiProductCatalogVersionsSelector.selectedOptions()
        ).toBe(OPTION_PRODUCT_CATALOG_STAGE);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();

        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // THEN
        expect(
            await ExperienceSelectorObject.Elements.multiProductCatalogVersionsSelector.selectedOptions()
        ).toBe(OPTION_PRODUCT_CATALOG_STAGE);

        await ExperienceSelectorObject.Actions.productCatalogs.openMultiProductCatalogVersionsSelectorWidget();

        expect(
            await ExperienceSelectorObject.Elements.multiProductCatalogVersionsSelector
                .getSelectedOptionFromMultiProductCatalogVersionsSelectorWidget(
                    APPAREL_PRODUCT_CATALOG_CLOTHING.ID
                )
                .getText()
        ).toBe('Staged');
        expect(
            await ExperienceSelectorObject.Elements.multiProductCatalogVersionsSelector
                .getSelectedOptionFromMultiProductCatalogVersionsSelectorWidget(
                    APPAREL_PRODUCT_CATALOG_SHOES.ID
                )
                .getText()
        ).toBe('Staged-2');
    });

    it(
        'GIVEN I have selected an experience without a time WHEN I click the apply button AND the REST call to the' +
            " preview service succeeds THEN I expect the payload to match the API's expected payload",
        async () => {
            // GIVEN
            await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
            await ExperienceSelectorObject.Actions.catalog.selectDropdown();
            await ExperienceSelectorObject.Actions.catalog.selectOption(
                ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
            );

            await ExperienceSelectorObject.Actions.language.selectDropdown();
            await ExperienceSelectorObject.Actions.language.selectOption(
                APPAREL_SITE.LANGUAGES.ENGLISH
            );

            // WHEN
            await ExperienceSelectorObject.Actions.widget.submit();
            browser.waitForWholeAppToBeReady();

            // THEN
            const EXPECTED_URI_SUFFIX =
                '/apps/smartedit-e2e/generated/pages/storefront.html?cmsTicketId=validTicketId';
            expect(
                await ExperienceSelectorObject.Elements.page.iframe().getAttribute('src')
            ).toContain(EXPECTED_URI_SUFFIX);
        }
    );

    it(
        'GIVEN I have selected an experience with a time WHEN I click the apply button AND the REST call to the' +
            " preview service succeeds THEN I expect the payload to match the API's expected payload",
        async () => {
            // GIVEN
            await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
            await ExperienceSelectorObject.Actions.catalog.selectDropdown();
            await ExperienceSelectorObject.Actions.catalog.selectOption(
                ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
            );

            await ExperienceSelectorObject.Actions.language.selectDropdown();
            await ExperienceSelectorObject.Actions.language.selectOption(
                APPAREL_SITE.LANGUAGES.ENGLISH
            );

            await browser.click(await ExperienceSelectorObject.Elements.dateAndTime.field());
            await ExperienceSelectorObject.Actions.calendar.setDate('1/1/16 1:00 PM');

            // WHEN
            await ExperienceSelectorObject.Actions.widget.submit();
            await browser.waitForWholeAppToBeReady();

            // THEN
            const EXPECTED_URI_SUFFIX =
                'apps/smartedit-e2e/generated/pages/storefront.html?cmsTicketId=validTicketId';
            expect(
                await ExperienceSelectorObject.Elements.page.iframe().getAttribute('src')
            ).toContain(EXPECTED_URI_SUFFIX);
        }
    );

    it('GIVEN that I have deep linked and I have selected a new experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect to load the page to which I have deep linked without a preview ticket', async () => {
        await browser.linkAndBackToParent(by.id('deepLink'));
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.ONLINE
        );

        await browser.click(await ExperienceSelectorObject.Elements.dateAndTime.field());
        await ExperienceSelectorObject.Actions.calendar.setDate('1/1/16 1:00 PM');

        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(
            APPAREL_SITE.LANGUAGES.ENGLISH
        );

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();

        // THEN
        const EXPECTED_URI_SUFFIX =
            '/node_modules/@smartedit/storefront-generator/dummystorefront/dummystorefrontSecondPage.html';
        expect(await ExperienceSelectorObject.Elements.page.iframe().getAttribute('src')).toContain(
            EXPECTED_URI_SUFFIX
        );
    });

    // FIXME? not supported when storefront is served in different domain.
    xit('GIVEN that I have deep linked WHEN I select a new experience and the current page does not exist for this new experience THEN I will be redirected to the landing page of the new experience', async () => {
        // GIVEN
        await browser.linkAndBackToParent(by.id('deepLinkFailsWhenNewExperience'));
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(
            APPAREL_SITE.LANGUAGES.ENGLISH
        );

        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();

        const APPAREL_UK_ONLINE_HOMEPAGE = 'storefront.html?cmsTicketId=validTicketId';
        expect(await ExperienceSelectorObject.Elements.page.iframe().getAttribute('src')).toContain(
            APPAREL_UK_ONLINE_HOMEPAGE
        );
    });

    it('GIVEN I have selected an experience by setting the new field WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the new field set', async () => {
        const timestamp = '1/1/16 12:03 AM';
        // GIVEN
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();
        await ExperienceSelectorObject.Actions.catalog.selectDropdown();
        await ExperienceSelectorObject.Actions.catalog.selectOption(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.STAGED
        );

        await ExperienceSelectorObject.Actions.language.selectDropdown();
        await ExperienceSelectorObject.Actions.language.selectOption(
            APPAREL_SITE.LANGUAGES.ENGLISH
        );

        await ExperienceSelectorObject.Elements.otherFields
            .field('newField')
            .sendKeys('New Data For Preview');

        await browser.click(await ExperienceSelectorObject.Elements.dateAndTime.field());
        await ExperienceSelectorObject.Actions.calendar.setDate(timestamp);

        // WHEN
        await ExperienceSelectorObject.Actions.widget.submit();
        await browser.waitForWholeAppToBeReady();
        await ExperienceSelectorObject.Actions.widget.openExperienceSelector();

        // THEN
        expect(await ExperienceSelectorObject.Elements.catalog.selectedOption().getText()).toBe(
            ELECTRONICS_CONTENT_CATALOG.CATALOGS.STAGED
        );

        expect(
            await (await ExperienceSelectorObject.Elements.dateAndTime.field()).getAttribute(
                'value'
            )
        ).toBe(timestamp);
        expect(
            await ExperienceSelectorObject.Elements.otherFields
                .field('newField')
                .getAttribute('value')
        ).toBe('New Data For Preview');

        expect(
            await (await ExperienceSelectorObject.Elements.language.selectedOption()).getText()
        ).toBe('English');
    });
});
