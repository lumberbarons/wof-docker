(function () {
    'use strict';

    angular
        .module('MsgboardApp')
        .factory('Auth', Auth);

    Auth.$inject = ['$rootScope', '$state', '$http', 'localStorageService', '$q', 'Principal'];

    function Auth ($rootScope, $state, $http, localStorageService, $q, Principal) {
        var service = {
            authorize: authorize,
            login: login,
            logout: logout,
            getToken: getToken,
            hasValidToken: hasValidToken,
            getPreviousState: getPreviousState,
            resetPreviousState: resetPreviousState,
            storePreviousState: storePreviousState
        };
        return service;

        function authorize(force) {
            var authReturn = Principal.identity(force).then(authThen);

            return authReturn;

            function authThen () {
                var isAuthenticated = Principal.isAuthenticated();

                if ($rootScope.toState.data.authorities
                        && $rootScope.toState.data.authorities.length > 0
                        && !Principal.hasAnyAuthority($rootScope.toState.data.authorities)) {
                    if (isAuthenticated) {
                        $state.go('unauthorized');
                    } else {
                        storePreviousState($rootScope.toState.name, $rootScope.toStateParams);
                        $state.go('login');
                    }
                }
            }
        }

        function getToken() {
            var token = localStorageService.get('authenticationToken');
            return token;
        }

        function hasValidToken() {
            var token = this.getToken();
            return !!token;
        }

        function login (username, password) {
            var deferred = $q.defer();

            var body = {username: username, password: password};
            $http.post('/api/auth/token', body).then(function(response) {
                localStorageService.set('authenticationToken', response.data.token);
                Principal.identity(true).then(function(account) {
                    deferred.resolve();
                });
            }, function(response) {
                deferred.reject();
            });

            return deferred.promise;
        }

        function logout() {
            localStorageService.remove('authenticationToken');
            Principal.authenticate(null);
        }

        function getPreviousState() {
            var previousState = localStorageService.get('previousState');
            return previousState;
        }

        function resetPreviousState() {
            localStorageService.remove('previousState');
        }

        function storePreviousState(previousStateName, previousStateParams) {
            var previousState = { "name": previousStateName, "params": previousStateParams };
            localStorageService.set('previousState', previousState);
        }
    }
})();