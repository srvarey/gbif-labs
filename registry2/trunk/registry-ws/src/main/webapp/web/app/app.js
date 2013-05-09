angular.module('app', [
  'ui.compat', // the stateful angular router
  'search',
  'node',
  'resources.node'])

.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.when('', {redirectTo: '/search'});
}])

.run(['$rootScope', '$state', '$stateParams', function ($rootScope,   $state,   $stateParams) {
  $rootScope.$state = $state;
  $rootScope.$stateParams = $stateParams;
}]);