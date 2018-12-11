(function() {
    'use strict';

    angular
        .module('WofAdminApp')
        .config(loginConfig);

    loginConfig.$inject = ['$stateProvider'];

    function loginConfig($stateProvider) {
        $stateProvider.state('password', {
            parent: 'app',
            url: '/password',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/components/password/password.html',
                    controller: 'PasswordController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();