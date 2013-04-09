var registryApp = angular.module('registry.app', ['ui.compat', 'registry.node'])
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
      nodeApp.initializeState($stateProvider);
    }])
  .run (
      ['$rootScope', '$state', '$stateParams', function ($rootScope,   $state,   $stateParams) {
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;
      }]);
 
