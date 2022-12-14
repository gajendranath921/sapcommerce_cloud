/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { browser, Key } from 'protractor';

import { NotificationPanelComponentObject } from './NotificationPanelComponentObject';
import { Perspectives } from './Perspectives';

export namespace HotKeys {
    export const Constants = {
        HOTKEY_NOTIFICATION_ID: 'HOTKEY_NOTIFICATION_ID'
    };

    export const Actions = {
        async pressHotKeyModeSwitch(): Promise<void> {
            await browser.actions().sendKeys(Key.ESCAPE).perform();
        }
    };

    export const Assertions = {
        async assertHotkeyTooltipIconPresent(isPresent: boolean): Promise<void> {
            expect(await Perspectives.Elements.getTooltipIcon().isPresent()).toBe(isPresent);
        },

        async assertHotkeyNotificationPresence(): Promise<void> {
            await NotificationPanelComponentObject.Assertions.assertNotificationPresenceById(
                Constants.HOTKEY_NOTIFICATION_ID
            );
        },
        async assertHotkeyNotificationAbsence(): Promise<void> {
            await NotificationPanelComponentObject.Assertions.assertNotificationAbsenceById(
                Constants.HOTKEY_NOTIFICATION_ID
            );
        }
    };
}
