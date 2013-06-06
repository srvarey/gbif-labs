angular.module('installation', [
  'ngResource', 
  'services.notifications', 
  'contact',
  'endpoint',
  'identifier', 
  'tag', 
  'machinetag', 
  'comment'])

/**
 * Nested stated provider using dot notation (item.detail has a parent of item) and the 
 * nested view is rendered into the parent template ui.view div.  A single controller
 * governs the actions on the page. 
 */
.config(['$stateProvider', function ($stateProvider, $stateParams, Installation) {

  $stateProvider.state('installation', {
    url: '/installation/{key}',  
    abstract: true, 
    templateUrl: 'app/installation/installation-main.tpl.html',
    controller: 'InstallationCtrl',
    // resolve to lookup synchronously first, thus handling errors if not found
    resolve: {
      item: function(Installation, $state, $stateParams) {
        return Installation.getSync($stateParams.key);          
      }          
    },
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.detail', {  
    url: '',   
    templateUrl: 'app/installation/installation-overview.tpl.html',
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.edit', {
    url: '/edit',
    templateUrl: 'app/installation/installation-edit.tpl.html',
  })  
  .state('installation.contact', {  
    url: '/contact',   
    templateUrl: 'app/common/contact-list.tpl.html',
    controller: "ContactCtrl",  
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.endpoint', {  
    url: '/endpoint',   
    templateUrl: 'app/common/endpoint-list.tpl.html',
    controller: "EndpointCtrl",  
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.identifier', {  
    url: '/identifier',   
    templateUrl: 'app/common/identifier-list.tpl.html',
    controller: "IdentifierCtrl",  
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.tag', {  
    url: '/tag',   
    templateUrl: 'app/common/tag-list.tpl.html',
    controller: "TagCtrl",  
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.machinetag', {  
    url: '/machineTag',   
    templateUrl: 'app/common/machinetag-list.tpl.html',
    controller: "MachinetagCtrl",  
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.comment', {  
    url: '/comment',   
    templateUrl: 'app/common/comment-list.tpl.html',
    controller: "CommentCtrl",  
    context: 'installation', // necessary for reusing the components
  })
  .state('installation.dataset', {  
    url: '/dataset',   
    templateUrl: 'app/common/dataset-list.tpl.html',
    context: 'installation', // necessary for reusing the components
  })
}])

/**
 * RESTfully backed Installation resource
 */
.factory('Installation', function ($resource, $q) {
  var Installation = $resource('../installation/:key', {key : '@key'}, {
    save : {method:'PUT'}
  });  
 
   // A synchronous get, with a failure callback on error
  Installation.getSync = function (key, failureCb) {
    var deferred = $q.defer();
    Installation.get({key: key}, function(successData) {
      deferred.resolve(successData); 
    }, function(errorData) {
      deferred.reject(); // you could optionally pass error data here
      if (failureCb) {
        failureCb();
      }
    });
    return deferred.promise;
  };
  
  return Installation;
})

.filter('prettifyType', function () {
  return function(name) {
    switch (name) {
      case "BIOCASE_INSTALLATION": return "BioCASe";
      case "TAPIR_INSTALLATION": return "TAPIR";
      case "HTTP_INSTALLATION": return "HTTP";
      case "IPT_INSTALLATION": return "IPT";
      case "DIGIR_INSTALLATION": return "DiGIR";
      default: return name;  
    }
  };
})


/**
 * All operations relating to CRUD go through this controller. 
 */
.controller('InstallationCtrl', function ($scope, $state, $http, $resource, item, Installation, notifications) {
  $scope.installation = item;
  
  // TODO: should we clean this up?
  var typeLabel = $state.current.context;
  $scope.typeLabel = typeLabel.charAt(0).toUpperCase() + typeLabel.slice(1);
  
  
  // get the organization
  $http( { method:'GET', url: "../organization/" + item.organizationKey})
    .success(function (result) { $scope.organization = result});
  
  // populate the dropdowns
  var lookup = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {$scope[parameter] = result});
  }
	lookup('../enumeration/org.gbif.api.vocabulary.registry2.InstallationType','installationTypes');  
  
  // To enable the nested views update the counts, for the side bar
  $scope.counts = {
    // collesce with || and use _ for sizing
    contact : _.size($scope.installation.contacts || {}),
    endpoint : _.size($scope.installation.endpoints || {}), 
    identifier : _.size($scope.installation.identifiers || {}), 
    tag : _.size($scope.installation.tags || {}),
    machinetag : _.size($scope.installation.machineTags || {}),
    comment : _.size($scope.installation.comments || {})
  };
  
  var count = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {
        $scope.counts[parameter] = result.count;
        $scope[parameter] = result.results;
      });
  }
  count('../installation/' + $scope.installation.key + '/dataset?limit=1000','datasets');
	
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('installation.' + target, { key: item.key, type: "installation" }); 
  }
	
	$scope.save = function (installation) {
    Installation.save(installation, 
      function() {
        notifications.pushForNextRoute("Installation successfully updated", 'info');
        $scope.transitionTo("detail");
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      }
    );
  }
  
  $scope.openOrganization = function (organizationKey) {
    $state.transitionTo('organization.detail', {key : organizationKey});
  }  
  
  $scope.cancelEdit = function () {
    $scope.installation = Installation.get({ key: item.key });
    $scope.transitionTo("detail");
  }
  
  $scope.getDatasets = function () {
    return $scope.datasets;
  }
});