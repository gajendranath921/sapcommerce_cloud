/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
angular.module('cmsPerspectivesMocks', []).run(function (perspectiveService) {
    perspectiveService.register({
        key: 'someperspective',
        nameI18nKey: 'someperspective',
        descriptionI18nKey: 'someperspective',
        features: ['se.contextualMenu'],
        perspectives: []
    });
});
try {
    angular.module('smarteditcontainer').requires.push('cmsPerspectivesMocks');
} catch (e) {}
