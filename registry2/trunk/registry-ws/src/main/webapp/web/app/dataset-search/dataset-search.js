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
  .state('dataset-search.deleted', {  
    url: '/deleted',   
    templateUrl: 'app/dataset-search/dataset-deleted.tpl.html'
  })
  .state('dataset-search.duplicate', {  
    url: '/duplicate',   
    templateUrl: 'app/dataset-search/dataset-duplicate.tpl.html'
  })
  .state('dataset-search.subDataset', {  
    url: '/subDataset',   
    templateUrl: 'app/dataset-search/dataset-subDataset.tpl.html'
  })
  .state('dataset-search.withNoEndpoint', {  
    url: '/withNoEndpoint',   
    templateUrl: 'app/dataset-search/dataset-withNoEndpoint.tpl.html'
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
  
  // load quick lists
  $http.get('../dataset/deleted?limit=1000').success(function (data) {
    $scope.deletedCount = data.count;
    $scope.deleted = data.results;
  })
  $http.get('../dataset/duplicate?limit=1000').success(function (data) {
    $scope.duplicateCount = data.count;
    $scope.duplicate = data.results;
  })
  $http.get('../dataset/subDataset?limit=1000').success(function (data) {
    $scope.subDatasetCount = data.count;
    $scope.subDataset = data.results;
  })
  $http.get('../dataset/withNoEndpoint?limit=1000').success(function (data) {
    $scope.withNoEndpointCount = data.count;
    $scope.withNoEndpoint = data.results;
  })
  
  
  $scope.openDataset = function(dataset) {
    $state.transitionTo('dataset.detail', {key: dataset.key})
  }
  
  // TODO: should we start reusing functions? I mean seriously... this is copied from dataset.js
  // populate the dropdowns 
  var lookup = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {$scope[parameter] = result});
  }
	lookup('../enumeration/org.gbif.api.vocabulary.DatasetType','datasetTypes');
	lookup('../enumeration/org.gbif.api.vocabulary.DatasetSubtype','datasetSubTypes');
	lookup('../enumeration/org.gbif.api.vocabulary.Language','languages');  
	
	// sensible defaults for creation
	$scope.dataset = {};
	$scope.dataset.type="OCCURRENCE"; 
	$scope.dataset.language="ENGLISH"; 
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
          notifications.pushForCurrentRoute(response, 'error');
        });
    }        
  }
  
  $scope.cancelEdit = function() {
    $state.transitionTo('dataset-search.search'); 
  }  
});