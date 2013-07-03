angular.module('organization-search', [])

.config(['$stateProvider', function ($stateProvider, $stateParams, Organization) {
  $stateProvider.state('organization-search', {
    abstract: true,
    url: '/organization-search',  
    templateUrl: 'app/organization-search/organization-search.tpl.html',
    controller: 'OrganizationSearchCtrl',
  })
  .state('organization-search.search', {  
    url: '',
    templateUrl: 'app/organization-search/organization-results.tpl.html'
  })
  .state('organization-search.deleted', {  
    url: '/deleted',   
    templateUrl: 'app/organization-search/organization-deleted.tpl.html'
  })
  .state('organization-search.pending', {  
    url: '/pending',   
    templateUrl: 'app/organization-search/organization-pending.tpl.html'
  })
  .state('organization-search.nonPublishing', {  
    url: 'nonPublishing',   
    templateUrl: 'app/organization-search/organization-nonPublishing.tpl.html'
  })
  .state('organization-search.create', {  
    url: '/create',   
    templateUrl: 'app/organization/organization-edit.tpl.html',
    controller: 'OrganizationCreateCtrl',
    resolve: {
      item: function() { return {} } // load it with an empty one
    }
  })
}])

.controller('OrganizationSearchCtrl', function ($scope, $state, $http) {
  // load quick lists
  $http.get('../organization/deleted?limit=1000').success(function (data) {
    $scope.deletedCount = data.count;
    $scope.deleted = data.results;
  })
  $http.get('../organization/pending?limit=1000').success(function (data) {
    $scope.pendingCount = data.count;
    $scope.pending = data.results;
  })
  $http.get('../organization/nonPublishing?limit=1000').success(function (data) {
    $scope.nonPublishingCount = data.count;
    $scope.nonPublishing = data.results;
  })
  
  $scope.search = function(q) {
    $http.get('../organization?q=' + q).success(function (data) {
      $scope.resultsCount = data.count;
      $scope.results = data.results;
      $scope.searchString = q;
    });
  }
  $scope.search(""); // start with empty search
  
  $scope.openOrganization = function(organization) {
    $state.transitionTo('organization.detail', {key: organization.key})
  }
})


.controller('OrganizationCreateCtrl', function ($scope, $state, $http, $resource, item, notifications) {
  $scope.save = function (organization) {
    if (organization != undefined) {
      // TODO
      organization.endorsingNodeKey='d897a5b9-35ee-4232-94bd-b0bcaac003c2';
      organization.createdBy='TODO security in organization';
      organization.modifiedBy='TODO security in organization';
      organization.language='ENGLISH';
    
      // backend returns a string for create, so can't use resource
      $http.post("../organization", organization)
        .success(function(data) { 
          notifications.pushForNextRoute("Organization successfully updated", 'info');
          // strip the quotes
          $state.transitionTo('organization.detail', { key: data.replace(/["]/g,''), type: "organization" }); 
        })
        .error(function(response) {
          notifications.pushForCurrentRoute(response, 'error');
        });
    }
  }
  
  $scope.cancelEdit = function() {
    $state.transitionTo('organization-search.search'); 
  }  
});