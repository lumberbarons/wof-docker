(function() {
    'use strict';

    angular
        .module('MsgboardApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/components/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();