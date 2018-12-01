(function () {
    'use strict';

    angular
        .module('MsgboardApp')
        .factory('Minutes', Minutes);

    Minutes.$inject = ['$resource'];

    function Minutes ($resource) {
        return $resource('/api/hmgt/minutes/:id', null,
            {
                'update': { method:'PUT' }
            }
        );
    }
})();