angular.module('contact', [])

.controller('ContactCtrl', function ($scope, $state, $stateParams, $resource) {
  // help provide context with a label to the user
  var typeLabel = $state.current.context;
  $scope.typeLabel = typeLabel.charAt(0).toUpperCase() + typeLabel.slice(1);

  // keep a copy to allow a revert on cancelling the edit
  $scope.orig = angular.copy($scope.contacts);
	
  $scope.types = [
    'ADMINISTRATIVE',
    'TECHNICAL'
  ];	
  	
  var Contact = $resource('../:type/:key/contact/:contactKey', {
    type : $state.current.context, // this context should be set in the parent statemachine (e.g. node)
    key : $stateParams.key,  
    contactKey : '@id'}, {
      save : {method:'PUT'}
  });  

  // loads the identifiers, and updates the scope
  var refreshScope = function() {
    Contact.query(function(data) {
      $scope.contacts = data;
      $scope.counts.contact = data.length; // update parent counts
    });
  }
  refreshScope();
  

  $scope.save = function(item) {
    Contact.save(item);  
  }

  $scope.cancelEdit = function(index) {
    // reset only the one being cancelled
    $scope.contacts[index] = angular.copy($scope.orig[index]);
  }
    
});