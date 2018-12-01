(function() {
    'use strict';

    angular
        .module('MsgboardApp')
        .config(stateConfig)
        .config(materialConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('app', {
            abstract: true,
            views: {
                'navbar@': {
                    templateUrl: 'app/navigation/navbar.html',
                    controller: 'NavbarController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ]
            }
        });
    }

    materialConfig.$inject = ['$mdThemingProvider'];
    function materialConfig($mdThemingProvider) {
        $mdThemingProvider.theme('default')
          .primaryPalette('blue-grey');
    }

    angular.module('MsgboardApp').config(function (localStorageServiceProvider) {
      localStorageServiceProvider
        .setPrefix('MsgboardApp');
    });
})();
