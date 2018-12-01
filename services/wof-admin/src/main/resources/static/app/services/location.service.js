(function () {
    'use strict';

    angular
        .module('MsgboardApp')
        .factory('Location', Location);

    Location.$inject = ['$resource'];

    function Location($resource) {
        return $resource('/api/hmgt/locations/:id', null,
            { 'update': { method:'PUT' } }
        );
    }
})();