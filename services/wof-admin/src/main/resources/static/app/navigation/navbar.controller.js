(function() {
    'use strict';

    angular
        .module('MsgboardApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['localStorageService', '$state', '$rootScope'];

    function NavbarController(localStorageService, $state, $rootScope) {
        var vm = this;
    }
})();