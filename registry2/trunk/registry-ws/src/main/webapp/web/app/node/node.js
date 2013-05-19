angular.module('node', ['ngResource', 'resources.node', 'services.notifications', 'identifier'])

/**
 * Nested stated provider using dot notation (item.detail has a parent of item) and the 
 * nested view is rendered into the parent template ui.view div.  A single controller
 * governs the actions on the page. 
 */
.config(['$stateProvider', function ($stateProvider, $stateParams, Node) {
  $stateProvider.state('node', {
    url: '/{type}/{key}',  // {type} to provide context to things like identifier 
    abstract: true, 
    templateUrl: 'app/node/node-main.tpl.html',
    controller: 'NodeCtrl',
    // resolve to lookup synchronously first, thus handling errors if not found
    resolve: {
      item: function(Node, $state, $stateParams) {
        return Node.getSync($stateParams.key);          
      }          
    }
  })
  .state('node.detail', {  
    url: '',   
    templateUrl: 'app/node/node-overview.tpl.html'
  })
  .state('node.edit', {
    url: '/edit',
    templateUrl: 'app/node/node-edit.tpl.html',
  })  
  .state('node.identifier', {  
    url: '/identifier',   
    templateUrl: 'app/identifier/identifier-list.tpl.html',
    controller: "IdentifierCtrl",  
  })
}])

.controller('NodeCtrl', function ($scope, $state, $resource, item, Node, notifications) {
  $scope.node = item;
  //$scope.identifiers = $scope.node.identifiers;
  
  // To enable the nested views update the counts, for the side bar
  $scope.counts = {
    identifier : $scope.node.identifiers.length,
    tag : $scope.node.tags.length,
    machineTag : $scope.node.machineTags.length,
    comments : $scope.node.comments.length
  };
	
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('node.' + target, { key: item.key, type: "node" }); 
  }
	
	$scope.save = function (node) {
    Node.save(node, 
      function() {
        notifications.pushForNextRoute("Node successfully updated", 'info');
        $scope.transitionTo("detail");
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      }
    );
  }
  
  $scope.cancelEdit = function () {
    $scope.node = Node.get({ key: item.key });
    $scope.transitionTo("detail");
  }
});