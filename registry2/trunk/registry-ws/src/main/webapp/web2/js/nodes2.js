
// The node routing module
var nodeApp = angular.module('registry.node2', ['ngResource']).
  config(function($routeProvider) {
    $routeProvider.
      when('/node2/:nodeKey', {templateUrl: 'templates/node-detail2.html', controller: 'NodeDetail2'}).
      when('/node2/:nodeKey/edit', {templateUrl: 'templates/node-edit2.html', controller: 'NodeDetail2'}).
      otherwise({redirectTo: '/node/867069da-af23-45d9-b09e-cb48ce10b805'});
});
 
// RESTfully backed Node resource 
nodeApp.factory('Node2', function($resource) {
  // Node using key as the ID, and PUT on save (TODO change save to create)
  return $resource('/node/:nodeKey', {nodeKey : '@key'}, {
    save : {method:'PUT'}
  });  
});

// Search nodes control  
nodeApp.controller('NodeSearch2', function($scope, $routeParams, Node2) {
  $scope.nodes = Node.get();
});

// Single node detail / update control  
nodeApp.controller('NodeDetail2', function($scope, $routeParams, $location, $route, Node2) {
  $scope.node = Node2.get({nodeKey: $routeParams.nodeKey});  	  
  
  $scope.save = function(node) {
    // if (node.key) TODO: check if null and save or create
    Node2.save(node);
    $location.path('/node2/' + node.key);
  }
  
  $scope.cancelEdit = function(node) {
    $location.path('/node2/' + node.key);
  }
  
  $scope.edit = function(node) {
    $location.path('/node2/' + node.key + "/edit");
  }
});
