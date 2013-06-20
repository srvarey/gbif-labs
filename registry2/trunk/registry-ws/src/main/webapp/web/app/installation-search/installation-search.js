angular.module('installation-search', [])

.config(['$stateProvider', function ($stateProvider, $stateParams, Installation) {
  $stateProvider.state('installation-search', {
    abstract: true,
    url: '/installation-search',  
    templateUrl: 'app/installation-search/installation-search.tpl.html',
    controller: 'InstallationSearchCtrl',
  })
  .state('installation-search.search', {  
    url: '',
    templateUrl: 'app/installation-search/installation-results.tpl.html'
  })
  .state('installation-search.deleted', {  
    url: '/deleted',   
    templateUrl: 'app/installation-search/installation-deleted.tpl.html'
  })
  .state('installation-search.nonPublishing', {  
    url: 'nonPublishing',   
    templateUrl: 'app/installation-search/installation-nonPublishing.tpl.html'
  })  
  .state('installation-search.create', {  
    url: '/create',   
    templateUrl: 'app/installation/installation-edit.tpl.html',
    controller: 'InstallationCreateCtrl',
    resolve: {
      item: function() { return {} } // load it with an empty one
    }
  })
}])

.controller('InstallationSearchCtrl', function ($scope, $state, $http) {
  $scope.search = function(q) {
    $http.get('../installation?q=' + q).success(function (data) {
      $scope.resultsCount = data.count;
      $scope.results = data.results;
      $scope.searchString = q;
    });
  }
  $scope.search(""); // start with empty search
  
  // load quick lists
  $http.get('../installation/deleted?limit=1000').success(function (data) {
    $scope.deletedCount = data.count;
    $scope.deleted = data.results;
  })
  $http.get('../installation/nonPublishing?limit=1000').success(function (data) {
    $scope.nonPublishingCount = data.count;
    $scope.nonPublishing = data.results;
  })
  
  
  $scope.openInstallation = function(installation) {
    $state.transitionTo('installation.detail', {key: installation.key})
  }
})


.controller('InstallationCreateCtrl', function ($scope, $state, $http, $resource, item, notifications) {
  $scope.save = function (installation) {
    if (installation != undefined) {
      // TODO
      installation.createdBy='TODO security in installation';
      installation.modifiedBy='TODO security in installation';
      installation.language='ENGLISH';
    
      // backend returns a string for create, so can't use resource
      $http.post("../installation", installation)
        .success(function(data) { 
          notifications.pushForNextRoute("Installation successfully updated", 'info');
          // strip the quotes
          $state.transitionTo('installation.detail', { key: data.replace(/["]/g,''), type: "installation" }); 
        })
        .error(function(response) {
          notifications.pushForCurrentRoute(response, 'error');
        });
    }
  }
  
  $scope.cancelEdit = function() {
    $state.transitionTo('installation-search.search'); 
  }  
});