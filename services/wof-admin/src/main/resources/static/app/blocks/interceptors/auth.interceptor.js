(function() {
    'use strict';

    angular
        .module('MsgboardApp')
        .factory('authInterceptor', authInterceptor);

    authInterceptor.$inject = ['$q', '$injector'];

    function authInterceptor($q, $injector) {
        var service = {
            request: request
        };
        return service;

        function request(config) {
            var Auth = $injector.get('Auth');
            if(Auth.hasValidToken()) {
                config.headers.Authorization = "Bearer " + Auth.getToken()
            }
            return config;
        }
    }
})();