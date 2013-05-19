// A reusable directive for identifiers
angular.module('identifier', [])

.controller('IdentifierCtrl', function ($scope, $state, $stateParams, $resource) {
  // keep a copy to allow a revert on cancelling the edit
  $scope.orig = angular.copy($scope.identifiers);
	
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
  
  var Identifier = $resource('../:type/:key/identifier/:identifierKey', {
    type : $stateParams.type,
    key : $stateParams.key,  
    identifierKey : '@key'}, {
      save : {method:'PUT'}
  });  
  

  console.log($stateParams.type);

  $scope.save = function(item) {
    Identifier.save(item);  
  }

  $scope.cancelEdit = function(index) {
    // reset only the one being cancelled
    $scope.identifiers[index] = angular.copy($scope.orig[index]);
  }
    
});