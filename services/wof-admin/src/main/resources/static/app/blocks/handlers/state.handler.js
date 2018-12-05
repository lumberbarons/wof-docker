(function() {
    'use strict';

    angular
        .module('WofAdminApp')
        .factory('stateHandler', stateHandler);

    stateHandler.$inject = ['$rootScope', '$state', 'Auth', 'Principal'];

    function stateHandler($rootScope, $state, Auth, Principal) {
        return {
            initialize: initialize
        };

        function initialize() {
            var stateChangeStart = $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams, fromState) {
                $rootScope.toState = toState;
                $rootScope.toStateParams = toStateParams;

                if(Principal.isIdentityResolved()) {
                    Auth.authorize();
                }
            });

            $rootScope.$on('$destroy', function () {
                if(angular.isDefined(stateChangeStart) && stateChangeStart !== null){
                    stateChangeStart();
                }
            });
        }
    }
})();