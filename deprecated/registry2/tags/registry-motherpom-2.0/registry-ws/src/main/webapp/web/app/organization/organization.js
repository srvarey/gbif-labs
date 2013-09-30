angular.module('organization', [
  'ngResource', 
  'services.notifications', 
  'contact',
  'organization',
  'identifier', 
  'tag', 
  'machinetag', 
  'comment'])

/**
 * Nested stated provider using dot notation (item.detail has a parent of item) and the 
 * nested view is rendered into the parent template ui.view div.  A single controller
 * governs the actions on the page. 
 */
.config(['$stateProvider', function ($stateProvider, $stateParams, Organization) {
  $stateProvider.state('organization', {
    url: '/organization/{key}',  
    abstract: true, 
    templateUrl: 'app/organization/organization-main.tpl.html',
    controller: 'OrganizationCtrl',
    // resolve to lookup synchronously first, thus handling errors if not found
    resolve: {
      item: function(Organization, $state, $stateParams) {
        return Organization.getSync($stateParams.key);          
      }          
    }
  })
  .state('organization.detail', {  
    url: '',   
    templateUrl: 'app/organization/organization-overview.tpl.html'
  })
  .state('organization.edit', {
    url: '/edit',
    templateUrl: 'app/organization/organization-edit.tpl.html',
  })  
  .state('organization.contact', {  
    url: '/contact',   
    templateUrl: 'app/common/contact-list.tpl.html',
    controller: "ContactCtrl",  
    context: 'organization', // necessary for reusing the components
    heading: 'Organization contacts', // title for the sub pane 
  })
  .state('organization.endpoint', {  
    url: '/endpoint',   
    templateUrl: 'app/common/endpoint-list.tpl.html',
    controller: "EndpointCtrl",  
    context: 'organization', 
    heading: 'Organization endpoints', 
  })
  .state('organization.identifier', {  
    url: '/identifier',   
    templateUrl: 'app/common/identifier-list.tpl.html',
    controller: "IdentifierCtrl",  
    context: 'organization', 
    heading: 'Organization identifiers', 
  })
  .state('organization.tag', {  
    url: '/tag',   
    templateUrl: 'app/common/tag-list.tpl.html',
    controller: "TagCtrl",  
    context: 'organization',
    heading: 'Organization tags', 
  })
  .state('organization.machinetag', {  
    url: '/machineTag',   
    templateUrl: 'app/common/machinetag-list.tpl.html',
    controller: "MachinetagCtrl",  
    context: 'organization', 
    heading: 'Organization machine tags', 
  })
  .state('organization.comment', {  
    url: '/comment',   
    templateUrl: 'app/common/comment-list.tpl.html',
    controller: "CommentCtrl",  
    context: 'organization', 
    heading: 'Organization comments', 
  })
  .state('organization.owned', {  
    url: '/owned',   
    templateUrl: 'app/common/dataset-list.tpl.html',
    context: 'organization',
    heading: 'Datasets published by the organization', 
  })
  .state('organization.hosted', {  
    url: '/hosted',   
    templateUrl: 'app/common/dataset-hosted-list.tpl.html',
    context: 'organization', 
    heading: 'Datasets hosted by the organization', 
  })
  .state('organization.installation', {  
    url: '/installation',   
    templateUrl: 'app/common/installation-list.tpl.html',
    context: 'organization', // necessary for reusing the components
    heading: 'Installations hosted by the organization', 
  })  
  
}])

/**
 * RESTfully backed Organization resource
 */
.factory('Organization', function ($resource, $q) {
  var Organization = $resource('../organization/:key', {key : '@key'}, {
    save : {method:'PUT'},
    create : {method:'POST'},
  });  
  
  // A synchronous get, with a failure callback on error
  Organization.getSync = function (key, failureCb) {
    var deferred = $q.defer();
    Organization.get({key: key}, function(successData) {
      deferred.resolve(successData); 
    }, function(errorData) {
      deferred.reject(); // you could optionally pass error data here
      if (failureCb) {
        failureCb();
      }
    });
    return deferred.promise;
  };
  
  return Organization;
})

/**
 * All operations relating to CRUD go through this controller. 
 */
.controller('OrganizationCtrl', function ($scope, $state, $http, $resource, item, Organization, notifications) {
  $scope.organization = item;
    
  // To enable the nested views update the counts, for the side bar
  $scope.counts = {
    // collesce with || and use _ for sizing
    contact : _.size($scope.organization.contacts || {}),
    endpoint : _.size($scope.organization.endpoints || {}),
    identifier : _.size($scope.organization.identifiers || {}), 
    tag : _.size($scope.organization.tags || {}),
    machinetag : _.size($scope.organization.machineTags || {}),
    comment : _.size($scope.organization.comments || {})
  };
  
  // get the endorsing node if there is one
  $http( { method:'GET', url: "../node/" + item.endorsingNodeKey})
    .success(function (result) { $scope.endorsingNode = result});
  
  var count = function(url, parameter) {
    $http( { method:'GET', url: url}).success(function (result) {
      $scope.counts[parameter] = result.count;
      $scope[parameter] = result.results;
    });
  }
  count('../organization/' + $scope.organization.key + '/ownedDataset','ownedDatasets');
  count('../organization/' + $scope.organization.key + '/installation','installations');
  $http( { method:'GET', url: '../organization/' + $scope.organization.key + '/tag'})
    .success(function (result) {$scope.counts['tag'] =  _.size(result || {})});
  

  // populate the titles of the organization the hosted datasets are hosted for 
  $http( { method:'GET', url: '../organization/' + $scope.organization.key + '/hostedDataset?limit=1000'}).success(function (result) {
    $scope['hostedDatasets'] = result.results;
    $scope.counts['hostedDatasets'] = result.count;
      
    $.each(result.results, function(index, dataset) {
      $http( { method:'GET', url: '../organization/' + dataset.owningOrganizationKey}).success(function (organization) {
        dataset.owningOrganizationTitle = organization.title;
      });
    });
  });
	
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('organization.' + target, { key: item.key, type: "organization" }); 
  }
	
	$scope.save = function (organization) {
    Organization.save(organization, 
      function() {
        notifications.pushForNextRoute("Organization successfully updated", 'info');
        $scope.transitionTo("detail");
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      }
    );
  }
  
  $scope.openNode = function (nodeKey) {
    $state.transitionTo('node.detail', {key : nodeKey});
  }  
  
  $scope.cancelEdit = function () {
    $scope.organization = Organization.get({ key: item.key });
    $scope.transitionTo("detail");
  }
  
  $scope.getDatasets = function() {
    if ($state.includes('organization.hosted')) {
      return $scope['hostedDatasets'];
    } else {
      return $scope['ownedDatasets'];
    }
  }
});