angular.module('app', [
  'ui.compat', // the stateful angular router
  'ngSanitize', // for the like	 of bind-html
  'ngCookies', // for security
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
  'resources',  
  ])

.config(['$routeProvider', 'RestangularProvider', '$httpProvider', function ($routeProvider, RestangularProvider, $httpProvider) {
  // TODO: no idea, why angular starts up redirecting to #/index.html, but this adds a second redirect
  $routeProvider.when('/index.html', {redirectTo: '/home'});
  
  // relative to /web brings us up to the root
  // should this be run outside of the registry-ws project, this will need changed
  RestangularProvider.setBaseUrl("../"); 
    
  // all GBIF entities use "key" and not "id" as the id, and this is used inn routing
  RestangularProvider.setRestangularFields({
    id: "key"
  });  
  
  // we really do not want 401 responses (or the dreaded browser login window appears)
  $httpProvider.defaults.headers.common['gbif-prefer-403-over-401']='true';
}])

// app constants are global in scope 
.constant('DEFAULT_PAGE_SIZE', 1000)

.controller('AppCtrl', function ($scope, notifications, $state, $rootScope, notifications, $cookieStore, authService, Auth) {
  // register global notifications once
  $scope.notifications = notifications;

  $scope.removeNotification = function (notification) {
    notifications.remove(notification);
  };
  
  $rootScope.$on('event:auth-loginRequired', function() {
    notifications.pushForNextRoute("Requires account with administrative rights", 'error');
    $rootScope.isLoggedIn = false;        
    $state.transitionTo("login");
  });
    
  $rootScope.logout = function() {
    Auth.clearCredentials();
  }
  
  $rootScope.isLoggedIn = authService.isLoggedIn(); // initialize with a default
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
