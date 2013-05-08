angular.module('node', ['ngResource', 'resources.node'])

/**
 * Nested stated provider using dot notation (item.detail has a parent of item) and the 
 * nested view is rendered into the parent template ui.view div.  A single controller
 * governs the actions on the page. 
 */
.config(['$stateProvider', function ($stateProvider, $stateParams, Node) {
  $stateProvider.state('node', {
    url: '/node/{nodeKey}',
    abstract: true, 
    templateUrl: 'app/node/node-main.tpl.html',
    controller: 'NodeCtrl',
    // resolve to lookup synchronously first, thus handling errors if not found
    resolve: {
      item: function(Node, $state, $stateParams) {
        return Node.getSync($stateParams.nodeKey);          
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
}])

.controller('NodeCtrl', function ($scope, $state, item) {
  $scope.node = item;

  $scope.edit = function (node) {
    $state.transitionTo('node.edit', { nodeKey: node.key }); 
  }
  
  $scope.cancel = function (node) {
    $state.transitionTo('node.detail', { nodeKey: node.key }); 
  }
});