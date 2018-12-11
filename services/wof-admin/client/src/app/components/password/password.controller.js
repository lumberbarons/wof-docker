(function() {
    'use strict';

    angular
        .module('WofAdminApp')
        .controller('PasswordController', PasswordController);

    PasswordController.$inject = ['$scope', '$state', 'Auth'];

    function PasswordController($scope, $state, Auth) {
        $scope.passwordForm = {};
        $scope.submit = function() {
            console.log($scope.Form);
            console.log($scope.passwordForm);
        };
    }
})();