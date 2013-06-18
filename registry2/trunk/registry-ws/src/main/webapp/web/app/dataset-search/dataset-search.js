angular.module('dataset-search', [])

.config(['$stateProvider', function ($stateProvider, $stateParams, Dataset) {
  $stateProvider.state('dataset-search', {
    abstract: true,
    url: '/dataset-search',  
    templateUrl: 'app/dataset-search/dataset-search.tpl.html',
    controller: 'DatasetSearchCtrl',
  })
  .state('dataset-search.search', {  
    url: '',
    templateUrl: 'app/dataset-search/dataset-results.tpl.html'
  })
  .state('dataset-search.create', {  
    url: '/create',   
    templateUrl: 'app/dataset/dataset-edit.tpl.html',
    controller: 'DatasetCreateCtrl',
    resolve: {
      item: function() { return {} } // load it with an empty one
    }
  })
}])

.controller('DatasetSearchCtrl', function ($scope, $state, $http) {
  $scope.search = function(q) {
    $http.get('../dataset?q=' + q).success(function (data) {
      $scope.resultsCount = data.count;
      $scope.results = data.results;
      $scope.searchString = q;
    });
  }
  $scope.search(""); // start with empty search
  
  $scope.openDataset = function(dataset) {
    $state.transitionTo('dataset.detail', {key: dataset.key})
  }
})


.controller('DatasetCreateCtrl', function ($scope, $state, $http, $resource, item, notifications) {
  $scope.save = function (dataset) {
    if (dataset != undefined) {
      // TODO
      dataset.createdBy='TODO security in dataset';
      dataset.modifiedBy='TODO security in dataset';
      dataset.language='ENGLISH';
    
      // backend returns a string for create, so can't use resource
      $http.post("../dataset", dataset)
        .success(function(data) { 
          notifications.pushForNextRoute("Dataset successfully updated", 'info');
          // strip the quotes
          $state.transitionTo('dataset.detail', { key: data.replace(/["]/g,''), type: "dataset" }); 
        })
        .error(function(response) {
          notifications.pushForCurrentRoute(response.data, 'error');
        });
    }
  }
  
  $scope.cancelEdit = function() {
    $state.transitionTo('dataset-search.search'); 
  }  
});