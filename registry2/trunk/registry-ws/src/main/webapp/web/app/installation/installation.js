angular.module('installation', [
  'restangular',
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
  })
  .state('installation.detail', {  
    url: '/',   
    templateUrl: 'app/installation/installation-overview.tpl.html',
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
    heading: 'Installation contacts', // title for the sub pane         
  })
  .state('installation.endpoint', {  
    url: '/endpoint',   
    templateUrl: 'app/common/endpoint-list.tpl.html',
    controller: "EndpointCtrl",  
    context: 'installation', 
    heading: 'Installation endpoints',
  })
  .state('installation.identifier', {  
    url: '/identifier',   
    templateUrl: 'app/common/identifier-list.tpl.html',
    controller: "IdentifierCtrl",  
    context: 'installation', 
    heading: 'Installation identifiers',
  })
  .state('installation.tag', {  
    url: '/tag',   
    templateUrl: 'app/common/tag-list.tpl.html',
    controller: "TagCtrl",  
    context: 'installation', 
    heading: 'Installation tags',
  })
  .state('installation.machinetag', {  
    url: '/machineTag',   
    templateUrl: 'app/common/machinetag-list.tpl.html',
    controller: "MachinetagCtrl",  
    context: 'installation', 
    heading: 'Installation machine tags',
  })
  .state('installation.comment', {  
    url: '/comment',   
    templateUrl: 'app/common/comment-list.tpl.html',
    controller: "CommentCtrl",  
    context: 'installation', 
    heading: 'Installation comments',
  })
  .state('installation.dataset', {  
    url: '/dataset',   
    templateUrl: 'app/common/dataset-list.tpl.html',
    context: 'installation', 
    heading: 'Datasets served by the installation',
  })
}])

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
.controller('InstallationCtrl', function ($scope, $state, $stateParams, $http, notifications, Restangular) {
  var key =  $stateParams.key;
  
  // shared across sub views
  $scope.counts = {}; 
  
  var load = function() {
    Restangular.one('installation', key).get()
    .then(function(installation) {
      $scope.installation = installation;
      $scope.counts.contact = _.size(installation.contacts); 
      
      // served datasets
      installation.getList('dataset', {limit: 1000})
        .then(function(response) {
          installation.datasets = response.results;
        });
        
      // the hosting organization
      installation.organization = Restangular.one('organization', installation.organizationKey).get();
    });
  }
  load();
  
  // populate the dropdowns
  var lookup = function(url, parameter) {
    $http( { method:'GET', url: url})
      .success(function (result) {$scope[parameter] = result});
  }
	lookup('../enumeration/org.gbif.api.vocabulary.registry2.InstallationType','installationTypes');  
  
	// transitions to a new view, correctly setting up the path
  $scope.transitionTo = function (target) {
    $state.transitionTo('installation.' + target, { key: key, type: "installation" }); 
  }
	
	$scope.save = function (installation) {
    installation.put().then( 
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
    load();
    $scope.transitionTo("detail");
  }
  
  $scope.delete = function (installation) {
    installation.remove().then(
      function() {
        notifications.pushForNextRoute("Installation successfully deleted", 'info');
        load();
        $scope.transitionTo("detail");
      }
    );
  }
  
  $scope.restore = function (installation) {
    installation.deleted = undefined;
    installation.put().then( 
      function() {
        notifications.pushForCurrentRoute("Installation successfully restored", 'info');
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      }
    );
  }
  
  $scope.getDatasets = function () {
    if ($scope.installation) return $scope.installation.datasets;
  }
});