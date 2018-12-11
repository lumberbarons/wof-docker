(function() {
    'use strict';

    angular
        .module('WofAdminApp')
        .config(loginConfig);

    loginConfig.$inject = ['$stateProvider'];

    function loginConfig($stateProvider) {
        $stateProvider.state('login', {
            parent: 'app',
            url: '/login',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/components/login/login.html',
                    controller: 'LoginController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();