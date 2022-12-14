/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { browser } from 'protractor';
import { NotificationPanelComponentObject } from '../../utils/components/NotificationPanelComponentObject';
import { NotificationPanelTestPageObject as PageObject } from './notificationPanelTestPageObject';

describe('notificationPanel', () => {
    describe('Notifications', () => {
        beforeEach(async () => {
            await PageObject.Actions.navigateToTestPage();
            await PageObject.Actions.resetForm();
            browser.waitUntilNoModal();
            await PageObject.Actions.removeAllNotifications();
        });

        describe('List', () => {
            describe('Push', async () => {
                it('A notification is present once it is added', async () => {
                    // Given
                    await PageObject.Actions.pushNotification(PageObject.Constants.NOTIFICATION_ID);

                    // Then
                    await NotificationPanelComponentObject.Assertions.assertNumberOfNotifications(
                        1
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationPresenceById(
                        PageObject.Constants.NOTIFICATION_ID
                    );
                });

                it('A notification is present at the top of the list once it is added', async () => {
                    // Given
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID1
                    );

                    // When
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID2
                    );

                    // Then
                    await NotificationPanelComponentObject.Assertions.assertNumberOfNotifications(
                        2
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationPresenceById(
                        PageObject.Constants.NOTIFICATION_ID2
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationIdByIndex(
                        0,
                        PageObject.Constants.NOTIFICATION_ID2
                    );
                });

                it('Notifications are displayed in reverse order in relation to addition order', async () => {
                    // Given
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID1
                    );
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID2
                    );
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID3
                    );

                    // Then
                    await NotificationPanelComponentObject.Assertions.assertNotificationIdByIndex(
                        0,
                        PageObject.Constants.NOTIFICATION_ID3
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationIdByIndex(
                        1,
                        PageObject.Constants.NOTIFICATION_ID2
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationIdByIndex(
                        2,
                        PageObject.Constants.NOTIFICATION_ID1
                    );
                });
            });

            describe('Remove', () => {
                it('A notification is no longer present once it is removed', async () => {
                    // Given
                    await PageObject.Actions.pushNotification(PageObject.Constants.NOTIFICATION_ID);

                    // When
                    await PageObject.Actions.removeNotification(
                        PageObject.Constants.NOTIFICATION_ID
                    );

                    // Then
                    await NotificationPanelComponentObject.Assertions.assertNumberOfNotifications(
                        0
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationAbsenceById(
                        PageObject.Constants.NOTIFICATION_ID
                    );
                });
            });

            describe('Remove All', () => {
                it('All notifications are no longer present once they are all removed', async () => {
                    // Given
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID1
                    );
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID2
                    );
                    await PageObject.Actions.pushNotification(
                        PageObject.Constants.NOTIFICATION_ID3
                    );

                    // When
                    await PageObject.Actions.removeAllNotifications();

                    // Then
                    await NotificationPanelComponentObject.Assertions.assertNumberOfNotifications(
                        0
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationAbsenceById(
                        PageObject.Constants.NOTIFICATION_ID1
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationAbsenceById(
                        PageObject.Constants.NOTIFICATION_ID2
                    );
                    await NotificationPanelComponentObject.Assertions.assertNotificationAbsenceById(
                        PageObject.Constants.NOTIFICATION_ID3
                    );
                });
            });
        });

        describe('Panel', () => {
            it('The panel disappears when it contains at least one notification and the mouse hovers over it', async () => {
                // Given
                await PageObject.Actions.pushNotification(PageObject.Constants.NOTIFICATION_ID);

                // When
                await NotificationPanelComponentObject.Actions.moveMousePointerOverNotificationPanel();

                // Then
                await NotificationPanelComponentObject.Assertions.assertNotificationPanelNotDisplayed();
            });

            it('The panel re-appears when it contains at least one notification and the mouse leaves its bounds after it had disappeared due to a mouse over', async () => {
                // Given
                await PageObject.Actions.pushNotification(PageObject.Constants.NOTIFICATION_ID);

                await NotificationPanelComponentObject.Actions.moveMousePointerOverNotificationPanel();

                // When
                await NotificationPanelComponentObject.Actions.moveMousePointerOutOfNotificationPanel();

                // Then
                await NotificationPanelComponentObject.Assertions.assertNotificationPanelDisplayed();
            });

            it('An element behind the notification panel is not clickable when a notification is displayed over it', async () => {
                // Given
                await PageObject.Actions.pushNotification(PageObject.Constants.NOTIFICATION_ID);

                // Then
                await PageObject.Assertions.assertClickThroughCheckboxNotClickable();
            });

            it('An element behind the notification panel is clickable when the mouse pointer is moved over the panel to hide it', async () => {
                // Given
                await PageObject.Actions.pushNotification(PageObject.Constants.NOTIFICATION_ID);
                await NotificationPanelComponentObject.Actions.moveMousePointerOverNotificationPanel();

                // When
                await PageObject.Actions.clickClickThroughCheckbox();

                // Then
                await PageObject.Assertions.assertClickThroughCheckboxSelected();
            });
        });
    });

    describe('Storefront', () => {
        describe('Panel', () => {
            beforeEach(async () => {
                await PageObject.Actions.navigateToStorefront();
            });

            it('The panel disappears when it contains at least one notification and the mouse hovers over it', async () => {
                // When
                await NotificationPanelComponentObject.Actions.moveMousePointerOverNotificationPanel();

                // Then
                await NotificationPanelComponentObject.Assertions.assertNotificationPanelNotDisplayed();
            });

            it('The panel re-appears when it contains at least one notification and the mouse leaves its bounds after it had disappeared due to a mouse over', async () => {
                // Given
                await NotificationPanelComponentObject.Actions.moveMousePointerOverNotificationPanel();

                // When
                await NotificationPanelComponentObject.Actions.moveMousePointerOutOfNotificationPanel();

                // Then
                await NotificationPanelComponentObject.Assertions.assertNotificationPanelDisplayed();
            });

            it('An element behind the notification panel is not clickable when a notification is displayed over it', async () => {
                await PageObject.Assertions.assertClickThroughCheckboxInIFrameNotClickable();
            });

            it('An element behind the notification panel is clickable when the mouse pointer is moved over the panel to hide it', async () => {
                // Given
                await NotificationPanelComponentObject.Actions.moveMousePointerOverNotificationPanel();

                // When
                await PageObject.Actions.clickClickThroughCheckboxInIFrame();

                // Then
                await PageObject.Assertions.assertClickThroughCheckboxSelected();
            });
        });
    });
});
