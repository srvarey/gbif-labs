
// The node routing module
var nodeModule = angular.module('registry.node', ['ngResource', 'ui.bootstrap.dropdownToggle']);

// The state machine
nodeModule.initializeState = function (stateProvider) {
  stateProvider.state('nodeSearch', {
    url: '/nodeSearch',
    templateUrl: 'templates/node/search.html',
    controller: 'NodeSearch'
  })
  .state('node', {
    url: '/node',
    abstract: true,
    templateUrl: 'templates/node/node.html'
  })
  .state('node.detail', {
    url: '/{nodeKey}',    
    views: {
      '': {
        templateUrl: 'templates/node/node.detail.html',
        controller: 'NodeDetail',
      },
      'title@node': {
        controller: 'NodeDetail',
        template: '{{node.title}}'
      }
    }
  })
  .state('node.edit', {
    url: '/{nodeKey}/edit',
    views: {
      '': {
        templateUrl: 'templates/node/node.edit.html',
        controller: 'NodeDetail',
      },
      'title@node': {
        controller: 'NodeDetail',
        template: '{{node.title}}'
      }
    }
  })
}

// RESTfully backed Node resource 
nodeModule.factory('Node', function($resource) {
  // Node using key as the ID, and PUT on save (TODO change save to create)
  return $resource('../node/:nodeKey', {nodeKey : '@key'}, {
    save : {method:'PUT'}
  });  
});

// Search nodes control  
nodeModule.controller('NodeSearch', function($scope, $state, Node) {
  $scope.nodes = Node.get();
  
  $scope.nodeDetail = function(node) {
    $state.transitionTo('node.detail', { nodeKey: node.key }); 
  }
});

// Single node detail / update control  
nodeModule.controller('NodeDetail', function($scope, $state, $stateParams, Node) {
  $scope.node = Node.get({nodeKey: $stateParams.nodeKey});  	  
  
  $scope.save = function(node) {
    // if (node.key) TODO: check if null and save or create
    Node.save(node, 
      function(response, headers) {
        $state.transitionTo('node.detail', { nodeKey: node.key })}, 
      function(response, headers) {
        $scope.errorMessage=response});
  }
  
  $scope.cancelEdit = function(node) {
    $state.transitionTo('node.detail', { nodeKey: node.key }); 
  }
  
  $scope.edit = function(node) {
    $state.transitionTo('node.edit', { nodeKey: node.key }); 
  }
});

// Inspired from http://stackoverflow.com/questions/14774486/use-jquery-timeago-and-angularjs-together
nodeModule.filter('timeAgo', function() {
  return function(date) {
    return moment(date).fromNow();
  }
});
