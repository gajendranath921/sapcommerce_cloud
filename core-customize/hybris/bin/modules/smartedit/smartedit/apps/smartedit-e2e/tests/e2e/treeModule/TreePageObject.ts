/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { browser, by, element, ElementArrayFinder, ElementFinder } from 'protractor';

const CLASS_SE_TREE_NODE_NAME = '.se-tree-node__name';
export namespace TreePageObject {
    export const Constants = {
        wrapperXpath() {
            return `//se-tree[contains(@id, 'angular-tree')]`;
        }
    };
    export const Elements = {
        getTree(): ElementFinder {
            return element(by.css(`#angular-tree`));
        },

        getAllNodeNameElements(): ElementArrayFinder {
            return element.all(
                by.xpath(
                    `${Constants.wrapperXpath()}//div[contains(@class, 'se-tree-node__name')]/span`
                )
            );
        },

        async getAllNodeNameString(): Promise<string[]> {
            const names = await Elements.getAllNodeNameElements().reduce(
                async (acc: string[], el: ElementFinder) => [...acc, await el.getText()],
                []
            );

            return names;
        },

        getOutsideButtonAddSibling(): ElementFinder {
            return element(by.css(`.angular-outside-add-sibling`));
        },

        getActionButton(label: string, buttonId: string): ElementFinder {
            return element(
                by.xpath(
                    `${Constants.wrapperXpath()}//div[contains(@class, 'se-tree-node__name')]
                    /span[contains(., '${label}')]/ancestor::se-tree-node-renderer[1]//a[contains(@class, 'angular-${buttonId}')]`
                )
            );
        },
        getChildren(label: string): ElementArrayFinder {
            return element.all(
                by.xpath(
                    `${Constants.wrapperXpath()}//div[contains(@class, 'se-tree-node__name')]
                    /span[contains(., '${label}')]/ancestor::se-tree-node-renderer[1]/se-tree-node[1]//div[contains(@class, 'se-tree-node__name')]/span`
                )
            );
        },
        getAddSiblingButton(label: string): ElementFinder {
            return Elements.getActionButton(label, 'new-sibling');
        },
        getAddChildButton(label: string): ElementFinder {
            return Elements.getActionButton(label, 'new-child');
        },
        getRemoveButton(label: string): ElementFinder {
            return Elements.getActionButton(label, 'remove');
        },
        getShowModalButton(label: string): ElementFinder {
            return Elements.getActionButton(label, 'show-modal');
        },
        getConfirmationModal(text: string): ElementFinder {
            return element(by.cssContainingText('#confirmationModalDescription', text));
        },
        getToggleButton(label: string): ElementFinder {
            return element(
                by.xpath(
                    `${Constants.wrapperXpath()}//div[contains(@class, 'se-tree-node__name')]
                    /span[contains(., '${label}')]/ancestor::se-tree-node-renderer[1]//a[contains(@class, 'se-tree-node__expander')]`
                )
            );
        },
        getNodeHandle(label: string): ElementFinder {
            return element(
                by.xpath(
                    `${Constants.wrapperXpath()}//div[contains(@class, 'se-tree-node__name')]
                    /span[contains(., '${label}')]/ancestor::li[1]`
                )
            );
        },
        async getNodeNameTitleAttribute(label: string): Promise<string> {
            const title = await element(by.xpath(`${Constants.wrapperXpath()}`))
                .element(by.cssContainingText(CLASS_SE_TREE_NODE_NAME, label))
                .getAttribute('title');

            return title;
        },
        getModalConfirmButton(): ElementFinder {
            return element(by.css('#confirmOk'));
        },
        getModalRejectButton(): ElementFinder {
            return element(by.css('#confirmCancel'));
        }
    };

    export const Actions = {
        async navigate(): Promise<void> {
            await browser.get('smartedit-e2e/generated/e2e/treeModule/#!/ng/storefront');
            await browser.waitForContainerToBeReady();
        },
        async addSiblingOutside(): Promise<void> {
            await browser.click(Elements.getOutsideButtonAddSibling());
        },
        async addSibling(label: string): Promise<void> {
            await browser.click(Elements.getAddSiblingButton(label));
        },
        async addChild(label: string): Promise<void> {
            await browser.click(Elements.getAddChildButton(label));
        },
        async remove(label: string): Promise<void> {
            await browser.click(Elements.getRemoveButton(label));
        },
        async showModal(label: string): Promise<void> {
            await browser.click(Elements.getShowModalButton(label));
        },
        async toggle(label: string): Promise<void> {
            await browser.click(Elements.getToggleButton(label));
        },
        async mouseUp(): Promise<void> {
            await browser.actions().mouseUp().perform();
        },
        async startDragging(label: string): Promise<void> {
            const node = Elements.getNodeHandle(label).element(by.css(CLASS_SE_TREE_NODE_NAME));

            await browser.actions().mouseMove(node).mouseDown().mouseMove({ x: 0, y: 5 }).perform();
        },
        async moveMouseToNode(label: string): Promise<void> {
            const node = Elements.getNodeHandle(label).element(by.css(CLASS_SE_TREE_NODE_NAME));

            await browser.actions().mouseMove(node).perform();
        },
        async moveMouseToNodeOffsetUp(label: string): Promise<void> {
            await Actions.moveMouseToNode(label).then(() => {
                browser
                    .actions()
                    .mouseMove({
                        x: 1,
                        y: -20
                    })
                    .perform();
            });
        },
        async confirmModal(): Promise<void> {
            await browser.click(Elements.getModalConfirmButton());
        },
        async rejectModal(): Promise<void> {
            await browser.click(Elements.getModalRejectButton());
        }
    };

    export const Assertions = {
        async hasNodeNames(nodeNames: string[]): Promise<void> {
            const names = await Elements.getAllNodeNameString();

            expect(names).toEqual(nodeNames);
        },

        async hasCorrectNodeCount(expected: number): Promise<void> {
            const count = await Elements.getAllNodeNameElements().count();
            expect(count).toBe(expected);
        },
        async hasCorrectChildrenCount(label: string, expected: number): Promise<void> {
            await browser.waitForPresence(Elements.getNodeHandle(label));
            const count = await Elements.getChildren(label).count();

            expect(count).toBe(expected);
        },
        async hasCorrectChildrenNames(label: string, names: string[]): Promise<void> {
            await browser.waitForPresence(Elements.getNodeHandle(label));
            const children = Elements.getChildren(label);

            for (let i = 0; i < names.length; i++) {
                expect(names.indexOf(await children.get(i).getText())).toBeGreaterThan(-1);
            }
        },
        async hasChildrenInCorrectOrder(label: string, names: string[]): Promise<void> {
            await browser.waitForPresence(Elements.getNodeHandle(label));
            const children = Elements.getChildren(label);

            for (let i = 0; i < names.length; i++) {
                expect(await children.get(i).getText()).toBe(names[i]);
            }
        },
        async modalContainingTextIsVisible(text: string): Promise<void> {
            await browser.waitForPresence(Elements.getConfirmationModal(text));
        },

        async nodeHasTitle(label: string, expectedTitle: string): Promise<void> {
            const title = await Elements.getNodeNameTitleAttribute(label);

            expect(title).toBe(expectedTitle);
        }
    };
}
