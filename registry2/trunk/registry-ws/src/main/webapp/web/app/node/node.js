angular.module('node', [
  'ngResource', 
  'services.notifications', 
  'resources',
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
.config(['$stateProvider', function ($stateProvider, $stateParams) {
  $stateProvider.state('node', {
    url: '/node/{key}',  
    abstract: true, 
    templateUrl: 'app/node/node-main.tpl.html',
    controller: 'NodeCtrl',
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
 * All operations relating to CRUD go through this controller. 
 */
.controller('NodeCtrl', function ($scope, $state, $stateParams, $resource, $http, notifications, NodeManager) {
  var key = $stateParams.key;
  $scope.node = NodeManager.get(key);
    
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
	lookup('../enumeration/org.gbif.api.vocabulary.ParticipationStatus','participationStatuses');
	lookup('../enumeration/org.gbif.api.vocabulary.GbifRegion','gbifRegions');
	lookup('../enumeration/org.gbif.api.vocabulary.Continent','continents');
	lookup('../enumeration/org.gbif.api.vocabulary.Country','countries');  
	
	// populate counts for sub resources
  var count = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {
        $scope.counts[parameter] = result.count;
        $scope[parameter] = result.results;
      });
  }
  //count('../node/' + key + '/pendingEndorsement','pendingEndorsements');
  count('../node/' + key + '/organization','organizations');
  count('../node/' + key + '/dataset','datasets');
  count('../node/' + key + '/installation','installations');
  $http( { method:'GET', url: '../node/' + key + '/tag'})
    .success(function (result) {$scope.counts['tag'] =  _.size(result || {})});
  
  NodeManager.pending(key, function(response) {
    $scope.pending = response.results;
    $scope.pendingCount = response.count;
  });
  
	
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('node.' + target, { key: key, type: "node" }); 
  }
	
	$scope.save = function (node) {
	  NodeManager.update(node);
  }
  
  $scope.cancelEdit = function () {
    $scope.node = NodeManager.get(key);
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