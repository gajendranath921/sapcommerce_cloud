/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc overview
 * @name ClickAnalyticsDummyDecoratorsModule
 * @description
 * Provides dummy decorators that make calls to mocked click analytics backend services on click. These decorators are
 * wired to components in the dummy storefront.
 */
angular
    .module('ClickAnalyticsDummyDecoratorsModule', [
        'jsonEndpointClickAnalyticsDecoratorModule',
        'htmlEndpointClickAnalyticsDecoratorModule'
    ])
    .run(function (decoratorService) {
        decoratorService.addMappings({
            componentType1: ['jsonEndpointClickAnalyticsDecorator'],
            componentType2: ['htmlEndpointClickAnalyticsDecorator']
        });

        decoratorService.enable('jsonEndpointClickAnalyticsDecorator');
        decoratorService.enable('htmlEndpointClickAnalyticsDecorator');
    });

/**
 * @ngdoc overview
 * @name jsonEndpointClickAnalyticsDecoratorModule
 * @description
 * Provides a simple decorator that makes a call to an endpoint returning JSON content type on click.
 */
angular
    .module('jsonEndpointClickAnalyticsDecoratorModule', [
        'ClickAnalyticsDummyDecoratorTemplatesModule',
        'ClickAnalyticsDummyDecoratorMocksModule'
    ])
    .directive('jsonEndpointClickAnalyticsDecorator', [
        'restServiceFactory',
        function (restServiceFactory) {
            return {
                templateUrl: 'jsonEndpointClickAnalyticsDecoratorTemplate.html',
                restrict: 'C',
                transclude: true,
                replace: false,
                controllerAs: 'ctrl',
                scope: {},
                bindToController: {
                    smarteditComponentId: '@',
                    smarteditComponentType: '@',
                    active: '='
                },
                controller: function () {
                    this.getClickAnalytics = function () {
                        restServiceFactory.get('jsonclickanalytics').get().then();
                    };
                }
            };
        }
    ]);

/**
 * @ngdoc overview
 * @name jsonEndpointClickAnalyticsDecoratorModule
 * @description
 * Provides a simple decorator that makes a call to an endpoint returning HTML content type on click.
 */
angular
    .module('htmlEndpointClickAnalyticsDecoratorModule', [
        'ClickAnalyticsDummyDecoratorTemplatesModule',
        'ClickAnalyticsDummyDecoratorMocksModule'
    ])
    .directive('htmlEndpointClickAnalyticsDecorator', [
        'restServiceFactory',
        function (restServiceFactory) {
            return {
                templateUrl: 'htmlEndpointClickAnalyticsDecoratorTemplate.html',
                restrict: 'C',
                transclude: true,
                replace: false,
                controllerAs: 'ctrl',
                scope: {},
                bindToController: {
                    smarteditComponentId: '@',
                    smarteditComponentType: '@',
                    active: '='
                },
                controller: function () {
                    this.getClickAnalytics = function () {
                        restServiceFactory.get('htmlclickanalytics').get().then();
                    };
                }
            };
        }
    ]);

/**
 * @ngdoc overview
 * @name AuthenticatedDummyDecoratorTemplatesModule
 * @description
 * Provides templates for the click analytics dummy decorators.
 */
angular.module('ClickAnalyticsDummyDecoratorTemplatesModule', []).run(function ($templateCache) {
    $templateCache.put(
        'jsonEndpointClickAnalyticsDecoratorTemplate.html',
        '<div>\n' +
            '    <div class="row">\n' +
            '        <div data-ng-transclude></div>\n' +
            '    </div>\n' +
            " <button id='submitButton' style='position: absolute; top: 0; left: 50px' data-ng-click='ctrl.getClickAnalytics()'>Get JSON</button>" +
            '</div>'
    );

    $templateCache.put(
        'htmlEndpointClickAnalyticsDecoratorTemplate.html',
        '<div>\n' +
            '    <div class="row">\n' +
            '        <div data-ng-transclude></div>\n' +
            '    </div>\n' +
            " <button id='secondaryButton' style='position: absolute; top: 0; left: 0' data-ng-click='ctrl.getClickAnalytics()'>Get HTML</button>" +
            '</div>'
    );
});

/**
 * @ngdoc overview
 * @name AuthenticatedDummyDecoratorTemplatesModule
 * @description
 * Provides mocks for the click analytics dummy decorators.
 */
angular.module('ClickAnalyticsDummyDecoratorMocksModule', []).run(function (httpBackendService) {
    httpBackendService.whenGET(/jsonclickanalytics/).respond(function () {
        return [
            404,
            null,
            {
                'Content-type': 'text/json'
            }
        ];
    });

    httpBackendService.whenGET(/htmlclickanalytics/).respond(function () {
        return [
            404,
            null,
            {
                'Content-type': 'text/html'
            }
        ];
    });
});
