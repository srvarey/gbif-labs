angular.module('node', [
  'ngResource', 
  'services.notifications', 
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
.config(['$stateProvider', function ($stateProvider, $stateParams, Node) {
  $stateProvider.state('node', {
    url: '/node/{key}',  
    abstract: true, 
    templateUrl: 'app/node/node-main.tpl.html',
    controller: 'NodeCtrl',
    // resolve to lookup synchronously first, thus handling errors if not found
    resolve: {
      item: function(Node, $state, $stateParams) {
        return Node.getSync($stateParams.key);          
      }          
    }
  })
  .state('node.detail', {  
    url: '',   
    templateUrl: 'app/node/node-overview.tpl.html'
  })
  .state('node.edit', {
    url: '/edit',
    templateUrl: 'app/node/node-edit.tpl.html',
  })  
  .state('node.contact', {  
    url: '/contact',   
    templateUrl: 'app/node/contact-list.tpl.html' // not common since read only
  })
  .state('node.identifier', {  
    url: '/identifier',   
    templateUrl: 'app/common/identifier-list.tpl.html',
    controller: "IdentifierCtrl",  
    context: 'node', // necessary for reusing the components
    heading: 'Node identifiers', // title for the sub pane         
  })
  .state('node.endpoint', {  
    url: '/endpoint',   
    templateUrl: 'app/common/endpoint-list.tpl.html',
    controller: "EndpointCtrl",  
    context: 'node', 
    heading: 'Node endpoints',          
  })
  .state('node.tag', {  
    url: '/tag',   
    templateUrl: 'app/common/tag-list.tpl.html',
    controller: "TagCtrl",  
    context: 'node', 
    heading: 'Node tags',          
  })
  .state('node.machinetag', {  
    url: '/machineTag',   
    templateUrl: 'app/common/machinetag-list.tpl.html',
    controller: "MachinetagCtrl",  
    context: 'node', 
    heading: 'Node machine tags',
  })
  .state('node.comment', {  
    url: '/comment',   
    templateUrl: 'app/common/comment-list.tpl.html',
    controller: "CommentCtrl",  
    context: 'node', 
    heading: 'Node comments',
  })
  .state('node.pending', {  
    url: '/pending',   
    templateUrl: 'app/common/organization-list.tpl.html',
    context: 'node', 
    heading: 'Organizations awaiting endorsement by the node',
  })
  .state('node.organization', {  
    url: '/organization',   
    templateUrl: 'app/common/organization-list.tpl.html',
    context: 'node', 
    heading: 'Organizations endorsed by the node',
  })
  .state('node.dataset', {  
    url: '/dataset',   
    templateUrl: 'app/common/dataset-list.tpl.html',
    context: 'node', 
    heading: 'Datasets published through the Nodes endorsement',
  })
  .state('node.installation', {  
    url: '/installation',   
    templateUrl: 'app/common/installation-list.tpl.html',
    context: 'node', 
    heading: 'Installations endorsed by the node',
  })  
}])

/**
 * RESTfully backed Node resource
 */
.factory('Node', function ($resource, $q) {
  var Node = $resource('../node/:key', {key : '@key'}, {
    save : {method:'PUT'}
  });  
  
  // A synchronous get, with a failure callback on error
  Node.getSync = function (key, failureCb) {
    var deferred = $q.defer();
    Node.get({key: key}, function(successData) {
      deferred.resolve(successData); 
    }, function(errorData) {
      deferred.reject(); // you could optionally pass error data here
      if (failureCb) {
        failureCb();
      }
    });
    return deferred.promise;
  };
  
  return Node;
})

/**
 * All operations relating to CRUD go through this controller. 
 */
.controller('NodeCtrl', function ($scope, $state, $resource, $http, item, Node, notifications) {
  $scope.node = item;
    
  // To enable the nested views update the counts, for the side bar
  $scope.counts = {
    // collesce with || and use _ for sizing
    contact : _.size($scope.node.contacts || {}), 
    identifier : _.size($scope.node.identifiers || {}), 
    endpoint : _.size($scope.node.endpoints || {}), 
    tag : _.size($scope.node.tags || {}),
    machinetag : _.size($scope.node.machineTags || {}),
    comment : _.size($scope.node.comments || {})
  };
  
  // populate the dropdowns
  var lookup = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {$scope[parameter] = result});
  }
	lookup('../enumeration/org.gbif.api.vocabulary.registry2.ParticipationStatus','participationStatuses');  
	lookup('../enumeration/org.gbif.api.vocabulary.registry2.GbifRegion','gbifRegions');  
	lookup('../enumeration/org.gbif.api.vocabulary.registry2.Continent','continents');  
	lookup('../enumeration/org.gbif.api.vocabulary.Country','countries');  
	
	// populate counts for sub resources
  var count = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {
        $scope.counts[parameter] = result.count;
        $scope[parameter] = result.results;
      });
  }
  count('../node/' + $scope.node.key + '/pendingEndorsement','pendingEndorsements');
  count('../node/' + $scope.node.key + '/organization','organizations');
  count('../node/' + $scope.node.key + '/dataset','datasets');
  count('../node/' + $scope.node.key + '/installation','installations');
  $http( { method:'GET', url: '../node/' + $scope.node.key + '/tag'})
    .success(function (result) {$scope.counts['tag'] =  _.size(result || {})});
  
	
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('node.' + target, { key: item.key, type: "node" }); 
  }
	
	$scope.save = function (node) {
    Node.save(node, 
      function() {
        notifications.pushForNextRoute("Node successfully updated", 'info');
        $scope.transitionTo("detail");
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      }
    );
  }
  
  $scope.cancelEdit = function () {
    $scope.node = Node.get({ key: item.key });
    $scope.transitionTo("detail");
  }
  
  // switch depending on the scope, which are visible
  $scope.getOrganizations = function() {
    if ($state.includes('node.pending')) {
      return $scope['pendingEndorsements'];
    } else {
      return $scope['organizations'];
    }
  }
  
  $scope.getDatasets = function() {
    return $scope['datasets'];
  }
});