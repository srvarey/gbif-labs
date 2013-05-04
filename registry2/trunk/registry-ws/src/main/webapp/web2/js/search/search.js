// The node routing module
var searchModule = angular.module('registry.search', ['ngResource', 'ui.bootstrap.dropdownToggle']);

// The state machine
searchModule.initializeState = function (stateProvider) {
  stateProvider.state('search', {
    url: '/search',
    templateUrl: 'templates/search/results.html',
    controller: 'Search'
  })
}

// Search control  
searchModule.controller('Search', function($scope, $state, $location, Node) {
  
  $scope.nodeDetail = function(node) {
    $state.transitionTo('node.detail', { nodeKey: node.key }); 
  }
  
  $scope.search = function() {
    var query = $scope.q;
    if(query === undefined) {
      query = "";
    } 
    alert(query);
    Node.get({q:query}, function(data) {
      $scope.nodes=data; 
      $state.transitionTo('search'); 
    });
  }  
  
  //$scope.search(); // initial load
});

// replace timestamps with the time ago
// Inspired from http://stackoverflow.com/questions/14774486/use-jquery-timeago-and-angularjs-together
searchModule.filter('timeAgo', function() {
  return function(date) {
    return moment(date).fromNow();
  }
});
