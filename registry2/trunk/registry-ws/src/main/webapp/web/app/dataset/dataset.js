angular.module('dataset', [
  'ngResource', 
  'services.notifications', 
  'contact', 
  'identifier', 
  'tag', 
  'machinetag', 
  'comment'])

/**
 * Nested stated provider using dot notation (item.detail has a parent of item) and the 
 * nested view is rendered into the parent template ui.view div.  A single controller
 * governs the actions on the page. 
 */
.config(['$stateProvider', function ($stateProvider, $stateParams, Dataset) {
  $stateProvider.state('dataset', {
    url: '/dataset/{key}',  // {type} to provide context to things like identifier 
    abstract: true, 
    templateUrl: 'app/dataset/dataset-main.tpl.html',
    controller: 'DatasetCtrl',
    // resolve to lookup synchronously first, thus handling errors if not found
    resolve: {
      item: function(Dataset, $state, $stateParams) {
        return Dataset.getSync($stateParams.key);          
      }          
    },
    data: {
      type: 'dataset', // provides the context to things like Identifier
    }
  })
  .state('dataset.detail', {  
    url: '',   
    templateUrl: 'app/dataset/dataset-overview.tpl.html'
  })
  .state('dataset.edit', {
    url: '/edit',
    templateUrl: 'app/dataset/dataset-edit.tpl.html',
  })  
  .state('dataset.identifier', {  
    url: '/identifier',   
    templateUrl: 'app/common/identifier-list.tpl.html',
    controller: "IdentifierCtrl",  
    context: 'dataset', // necessary for reusing the components
  })
  .state('dataset.contact', {  
    url: '/contact',   
    templateUrl: 'app/common/contact-list.tpl.html',
    controller: "ContactCtrl",  
    context: 'dataset', // necessary for reusing the components
  })
  .state('dataset.tag', {  
    url: '/tag',   
    templateUrl: 'app/common/tag-list.tpl.html',
    controller: "TagCtrl",  
    context: 'dataset', // necessary for reusing the components
  })
  .state('dataset.machinetag', {  
    url: '/machineTag',   
    templateUrl: 'app/common/machinetag-list.tpl.html',
    controller: "MachinetagCtrl",  
    context: 'dataset', // necessary for reusing the components
  })
  .state('dataset.comment', {  
    url: '/comment',   
    templateUrl: 'app/common/comment-list.tpl.html',
    controller: "CommentCtrl",  
    context: 'dataset', // necessary for reusing the components
  })
}])

/**
 * RESTfully backed Dataset resource
 */
.factory('Dataset', function ($resource, $q) {
  var Dataset = $resource('../dataset/:key', {key : '@key'}, {
    save : {method:'PUT'}
  });  
  
  // A synchronous get, with a failure callback on error
  Dataset.getSync = function (key, failureCb) {
    var deferred = $q.defer();
    Dataset.get({key: key}, function(successData) {
      deferred.resolve(successData); 
    }, function(errorData) {
      deferred.reject(); // you could optionally pass error data here
      if (failureCb) {
        failureCb();
      }
    });
    return deferred.promise;
  };
  
  return Dataset;
})

/**
 * All operations relating to CRUD go through this controller. 
 */
.controller('DatasetCtrl', function ($scope, $state, $resource, item, Dataset, notifications) {
  $scope.dataset = item;
  
  // To enable the nested views update the counts, for the side bar
  $scope.counts = {
    // collesce with || and use _ for sizing
    contact : _.size($scope.dataset.contacts || {}),
    identifier : _.size($scope.dataset.identifiers || {}), 
    tag : _.size($scope.dataset.tags || {}),
    machinetag : _.size($scope.dataset.machineTags || {}),
    comment : _.size($scope.dataset.comments || {})    
  };
	
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('dataset.' + target, { key: item.key, type: "dataset" }); 
  }
	
	$scope.save = function (dataset) {
    Dataset.save(dataset, 
      function() {
        notifications.pushForNextRoute("Dataset successfully updated", 'info');
        $scope.transitionTo("detail");
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      }
    );
  }
  
  $scope.cancelEdit = function () {
    $scope.dataset = Dataset.get({ key: item.key });
    $scope.transitionTo("detail");
  }
});