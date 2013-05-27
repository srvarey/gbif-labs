angular.module('installation', [
  'ngResource', 
  'services.notifications', 
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
    }
  })
  .state('installation.detail', {  
    url: '',   
    templateUrl: 'app/installation/installation-overview.tpl.html'
  })
  .state('installation.edit', {
    url: '/edit',
    templateUrl: 'app/installation/installation-edit.tpl.html',
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

/**
 * All operations relating to CRUD go through this controller. 
 */
.controller('InstallationCtrl', function ($scope, $state, $resource, item, Installation, notifications) {
  $scope.installation = item;
  
  // To enable the nested views update the counts, for the side bar
  $scope.counts = {
    // collesce with || and use _ for sizing
    identifier : _.size($scope.installation.identifiers || {}), 
    tag : _.size($scope.installation.tags || {}),
    machinetag : _.size($scope.installation.machineTags || {}),
    comment : _.size($scope.installation.comments || {})
  };
	
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
  
  $scope.cancelEdit = function () {
    $scope.installation = Installation.get({ key: item.key });
    $scope.transitionTo("detail");
  }
});