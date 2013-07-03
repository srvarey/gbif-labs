angular.module('endpoint', ['services.notifications'])

.controller('EndpointCtrl', function ($scope, $state, $stateParams, $resource, notifications) {
  // help provide context with a label to the user
  var typeLabel = $state.current.context;
  $scope.typeLabel = typeLabel.charAt(0).toUpperCase() + typeLabel.slice(1);

  var Endpoint = $resource('../:type/:key/endpoint/:endpointKey', {
    type : $state.current.context, // this context should be set in the parent statemachine (e.g. node)
    key : $stateParams.key,  
    endpointKey : '@id'}
  );
  
  // loads the endpoints, and updates the scope
  var refreshScope = function() {
    Endpoint.query(function(data) {
      $scope.endpoints = data;
      $scope.counts.endpoint = data.length; // update parent counts
    });
  }
  
  refreshScope();

  $scope.types = [
    'EML',
    'FEED',
    'WFS',
    'WMS',
    'TCS_RDF',
    'TCS_XML',
    'DWC_ARCHIVE',
    'DIGIR',
    'DIGIR_MANIS',
    'TAPIR',
    'BIOCASE',
    'OAI_PMH',
    'OTHER'  
  ];	
  
  $scope.save = function(item) {
    item.createdBy = "TODO: security for endpoint.js";
    item.modifiedBy = "TODO: security for endpoint.js";
    Endpoint.save(item,
      function() {
        notifications.pushForCurrentRoute("Endpoint successfully updated", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  }
  
  $scope.delete = function(endpoint) {
    Endpoint.delete({endpointKey : endpoint.key},
      function() {
        notifications.pushForCurrentRoute("Endpoint successfully deleted", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  
  }
});