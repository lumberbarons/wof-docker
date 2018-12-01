(function () {
    'use strict';

    angular
        .module('MsgboardApp')
        .factory('Locations', Locations);

    Locations.$inject = ['$resource'];

    function Locations ($resource) {
        return $resource('/api/hmgt/minutes/:minuteId/locations/:locationId');
    }
})();