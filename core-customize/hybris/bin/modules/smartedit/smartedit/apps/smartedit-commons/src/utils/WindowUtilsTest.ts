/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { WindowUtils } from 'smarteditcommons';

describe('Windows Utils Test - getTargetFrame', function () {
    let windowUtils: WindowUtils;
    let $window: any;
    let isIframeMock: jasmine.Spy;
    let getSmarteditIframe: jasmine.Spy;
    let isInCypressIframeMock: jasmine.Spy;

    beforeEach(() => {
        $window = {
            addEventListener: jasmine.createSpy('addEventListener'),
            document: jasmine.createSpyObj('document', ['getElementById']),
            parent: jasmine.createSpyObj('parent', ['top'])
        };

        windowUtils = new WindowUtils();
        spyOn(windowUtils, 'getWindow').and.returnValue($window);
        isIframeMock = spyOn(windowUtils, 'isIframe');
        getSmarteditIframe = spyOn(windowUtils, 'getSmarteditIframe');
        isInCypressIframeMock = spyOn(windowUtils, 'isInCypressIframe');
    });

    it('SHOULD return the parent frame if called within the iframe', function () {
        isIframeMock.and.returnValue(true);
        const targetFrame = windowUtils.getGatewayTargetFrame();
        expect(targetFrame).toBe($window.parent);
    });

    it('SHOULD return the iframe if called from the parent window', function () {
        const contentWindowContent = 'TestContentWindow' as any;

        $window.document = jasmine.createSpyObj('document', ['getElementById']);
        $window.document.getElementById.and.returnValue({
            contentWindow: contentWindowContent
        });

        getSmarteditIframe.and.returnValue(true);
        isInCypressIframeMock.and.returnValue(false);
        const targetFrame = windowUtils.getGatewayTargetFrame();
        expect($window.document.getElementById).toHaveBeenCalledWith('ySmartEditFrame');
        expect(targetFrame).toBe(contentWindowContent);
    });

    it('SHOULD return null when called from the parent and no iframe exists', function () {
        $window.document = jasmine.createSpyObj('document', ['getElementById']);
        $window.document.getElementById.and.returnValue(null);

        getSmarteditIframe.and.returnValue(true);
        expect(windowUtils.getGatewayTargetFrame()).toBeNull();
    });
});
