angular.module('search', []) 

.config(['$stateProvider', function ($stateProvider, $stateParams) {
  $stateProvider.state('search', {
    url: '/search?q',
    templateUrl:'app/search/search-list.tpl.html',
    controller: 'SearchViewCtrl',
    reloadOnSearch: true
  })
  
//  $routeProvider.when('/search', {
//    templateUrl:'app/search/search-list.tpl.html',
//    controller:'SearchViewCtrl',
//  });    
}])



// supports the functions of actions on the seach results
.controller('SearchViewCtrl', function($scope, $location, $stateParams) {
  $scope.q = $location.search().q;
  $scope.viewNode = function (nodeId) {
    $location.path('/node/'+nodeId);
  }
})


// performs the search
.controller('SearchCtrl', function($scope, $location, $stateParams, $state, $rootScope) {
  $scope.q = $location.search().q;
  
  $scope.search = function (query) {
    $location.search("q", query).path("/search");
  };    
})

;

