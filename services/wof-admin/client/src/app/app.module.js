(function() {
  'use strict';

  angular
    .module('WofAdminApp', [
        'ngResource',
        'ngMaterial',
        'ui.router',
        'md.data.table',
        'lfNgMdFileInput',
        'LocalStorageModule'
    ]).run(run);

  run.$inject = ['stateHandler'];

  function run(stateHandler) {
      stateHandler.initialize();
  }
})();
