angular.module('app', [
  'ui.compat', // the stateful angular router
  'ngSanitize', // for the likes of bind-html
  'services.notifications',
  'search',
  'dataset',
  'node',
  ])

.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.when('', {redirectTo: '/search'});
}])

.controller('AppCtrl', function ($scope, notifications) {
  // register global notifications once
  $scope.notifications = notifications;

  $scope.removeNotification = function (notification) {
    notifications.remove(notification);
  };
})

.run(['$rootScope', '$state', '$stateParams', function ($rootScope,   $state,   $stateParams) {
  $rootScope.$state = $state;
  $rootScope.$stateParams = $stateParams;
}]);
