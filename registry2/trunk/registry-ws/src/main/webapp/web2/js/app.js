var registryApp = angular.module('registry.app', ['ui.compat', 'registry.search', 'registry.node'])
  .config(
    ['$stateProvider', '$routeProvider', '$urlRouterProvider',
    function ($stateProvider,  $routeProvider,  $urlRouterProvider) {
      // bootstrap the URL here to '/'
      $urlRouterProvider.otherwise('/');
        
      $routeProvider.when('/', {        
        redirectTo: '/home'
      });
    
  	  $stateProvider
  	    .state('home', {
  	      url: '/home',
  	      templateUrl: 'templates/home/home.html'
        });
      
      // delegate to modules to add their states
      searchModule.initializeState($stateProvider);
      nodeModule.initializeState($stateProvider);
    }])
  .run (
      ['$rootScope', '$state', '$stateParams', function ($rootScope,   $state,   $stateParams) {
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;
      }]);
 
