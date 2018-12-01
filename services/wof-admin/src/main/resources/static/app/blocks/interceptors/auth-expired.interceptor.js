(function() {
    'use strict';

    angular
        .module('MsgboardApp')
        .factory('authExpiredInterceptor', authExpiredInterceptor);

    authExpiredInterceptor.$inject = ['$q', '$injector'];

    function authExpiredInterceptor($q, $injector) {
        var service = {
            responseError: responseError
        };
        return service;

        function responseError(rejection) {
            if (rejection.status === 401) {
                var Auth = $injector.get('Auth');
                Auth.logout();
            }
            return $q.reject(rejection);
        }
    }
})();