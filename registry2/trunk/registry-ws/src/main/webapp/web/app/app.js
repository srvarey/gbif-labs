angular.module('app', [
  'ui.compat', // the stateful angular router
  'ngSanitize', // for the likes of bind-html
  'ngCookies',
  'http-auth-interceptor', // intercepts 401 responses, and triggers login
  'restangular', // for REST calls
  'services.notifications',
  'home',
  'login',
  'search',
  'organization',
  'dataset',
  'installation',
  'node',
  'organization-search',
  'dataset-search',
  'node-search',
  'installation-search',
  'resources',
  
  ])

.config(['$routeProvider', 'RestangularProvider', '$httpProvider', function ($routeProvider, RestangularProvider, $httpProvider) {
  $routeProvider.when('', {redirectTo: '/home'});
  RestangularProvider.setBaseUrl("/");
  // all GBIF entities use "key" and not "id" as the id, and this is used inn routing
  RestangularProvider.setRestangularFields({
    id: "key"
  });  
  
  // we really do not want 401 responses, or the dreaded browser login window appears
  $httpProvider.defaults.headers.common['gbif-prefer-403-over-401']='true';
  
}])

// app constants are global in scope 
// TODO: get these from props? We'd want them to be different for live
.constant('NODE_URL', "../node")
.constant('DATASET_URL', "../dataset")
.constant('INSTALLATION_URL', "../installation")
.constant('ORGANIZATION_URL', "../organization")
.constant('DEFAULT_PAGE_SIZE', 1000)

.controller('AppCtrl', function ($scope, notifications, $state, $rootScope, notifications) {
  // register global notifications once
  $scope.notifications = notifications;

  $scope.removeNotification = function (notification) {
    notifications.remove(notification);
  };
  
  $rootScope.$on('event:auth-loginRequired', function() {
    notifications.pushForNextRoute("Requires account with Administrative rights", 'error');
    $state.transitionTo("login");
  });
})

// a safe array sizing filter  
.filter('size', function() {
  return function(input) {
    if (input) {
      return  _.size(input || {});
    } else {
      return 0;
    }    
  }
})

.run(['$rootScope', '$state', '$stateParams', function ($rootScope,   $state,   $stateParams) {
  $rootScope.$state = $state;
  $rootScope.$stateParams = $stateParams;
}]);
