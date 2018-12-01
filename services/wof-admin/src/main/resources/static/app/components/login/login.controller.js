(function() {
    'use strict';

    angular
        .module('MsgboardApp')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$scope', '$state', 'Auth'];

    function LoginController($scope, $state, Auth) {
        $scope.loginForm = {};
        $scope.submit = function() {
            Auth.login($scope.loginForm.username, $scope.loginForm.password).then(function() {
                if (Auth.getPreviousState()) {
                    var previousState = Auth.getPreviousState();
                    Auth.resetPreviousState();
                    $state.go(previousState.name, previousState.params);
                } else {
                    $state.go('minutes');
                }
            }, function() {
                // fail
            });
        };
    }
})();