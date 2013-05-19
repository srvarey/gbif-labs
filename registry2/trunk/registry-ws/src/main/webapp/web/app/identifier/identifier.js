// A reusable directive for identifiers
angular.module('identifier', ['services.notifications'])

.controller('IdentifierCtrl', function ($scope, $state, $stateParams, $resource, notifications) {
  var Identifier = $resource('../:type/:key/identifier/:identifierKey', {
    type : $stateParams.type,
    key : $stateParams.key,  
    identifierKey : '@id'}
  );
  
  // loads the identifiers, and updates the scope
  var refreshScope = function() {
    Identifier.query(function(data) {
      $scope.identifiers = data;
      $scope.counts.identifier = data.length; // update parent counts
    });
  }
  
  refreshScope();

  $scope.types = [
    'SOURCE_ID',
    'URL',
    'LSID',
    'HANDLER',
    'DOI',
    'UUID',
    'FTP',
    'URI',
    'UNKNOWN',
    'GBIF_PORTAL',
    'GBIF_NODE',
    'GBIF_PARTICIPANT'
  ];	
  
  $scope.save = function(item) {
    item.createdBy = "TODO: security for identifier.js";
    Identifier.save(item,
      function() {
        notifications.pushForCurrentRoute("Identifier successfully updated", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  }
  
  $scope.delete = function(identifier) {
    Identifier.delete({identifierKey : identifier.key},
      function() {
        notifications.pushForCurrentRoute("Identifier successfully deleted", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  
  }
});