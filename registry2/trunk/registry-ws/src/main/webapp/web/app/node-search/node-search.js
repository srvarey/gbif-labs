angular.module('node-search', [])

.config(['$stateProvider', function ($stateProvider, $stateParams, Node) {
  $stateProvider.state('node-search', {
    abstract: true,
    url: '/node-search',  
    templateUrl: 'app/node-search/node-search.tpl.html',
    controller: 'NodeSearchCtrl',
  })
  .state('node-search.search', {  
    url: '',
    templateUrl: 'app/node-search/node-results.tpl.html'
  })
  .state('node-search.create', {  
    url: '/create',   
    templateUrl: 'app/node/node-edit.tpl.html',
    controller: 'NodeCreateCtrl',
    resolve: {
      item: function() { return {} } // load it with an empty one
    }
  })
}])

.controller('NodeSearchCtrl', function ($scope, $state, $http) {
  $scope.search = function(q) {
    $http.get('../node?q=' + q).success(function (data) {
      $scope.resultsCount = data.count;
      $scope.results = data.results;
      $scope.searchString = q;
    });
  }
  $scope.search(""); // start with empty search
  
  $scope.openNode = function(node) {
    $state.transitionTo('node.detail', {key: node.key})
  }
})


.controller('NodeCreateCtrl', function ($scope, $state, $http, $resource, item, notifications) {
  $scope.save = function (node) {
    if (node != undefined) {
      // TODO
      node.createdBy='TODO security in node';
      node.modifiedBy='TODO security in node';
      node.language='ENGLISH';
    
      // backend returns a string for create, so can't use resource
      $http.post("../node", node)
        .success(function(data) { 
          notifications.pushForNextRoute("Node successfully updated", 'info');
          // strip the quotes
          $state.transitionTo('node.detail', { key: data.replace(/["]/g,''), type: "node" }); 
        })
        .error(function(response) {
          notifications.pushForCurrentRoute(response.data, 'error');
        });
    }
  }
  
  $scope.cancelEdit = function() {
    $state.transitionTo('node-search.search'); 
  }  
});