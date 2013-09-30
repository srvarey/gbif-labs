angular.module('contact', ['services.notifications'])

.controller('ContactCtrl', function ($scope, $state, $stateParams, $resource, notifications) {
  // help provide context with a label to the user
  var typeLabel = $state.current.context;
  $scope.typeLabel = typeLabel.charAt(0).toUpperCase() + typeLabel.slice(1);

	// array allows us to track which are open and reset on an edit
  $scope.editContact = [];
  
  $scope.types = [
    'ADMINISTRATIVE',
    'TECHNICAL'
  ];	
  	
  var Contact = $resource('../:type/:key/contact/:contactKey', {
    type : $state.current.context, // this context should be set in the parent statemachine (e.g. node)
    key : $stateParams.key,  
    contactKey : '@key'}, 
    {
      save : {method:'PUT'},
      create : {method:'POST'},
    });  

  // loads the contacts, and updates the scope
  var refreshScope = function() {
    Contact.query(function(data) {
      $scope.contacts = data;
      $scope.counts.contact = data.length; // update parent counts
      // keep a copy to allow a revert on cancelling the edit
      $scope.orig = angular.copy($scope.contacts);
    });
  }
  refreshScope();
  

  $scope.save = function(item) {
    item.createdBy = "TODO: security for contact.js";
    item.modifiedBy = "TODO: security for contact.js";
    
    var success = function() {
      notifications.pushForCurrentRoute("Contact successfully updated", 'info');
      // close any forms
      $.each($scope.editContact, function(i) {
        $scope.editContact[i] = false;
      }); 
      $scope.createNew = false;
      refreshScope();
    };
    
    var failure = function(response) {
      notifications.pushForCurrentRoute(response.data, 'error');
    };
    
    if (item.key != null) {
      Contact.save(item, success, failure);
    } else {
      Contact.create(item, success, failure);
    }
  }
  	

  $scope.edit = function(index) {
    // close the others
    $.each($scope.editContact, function(i) {
      $scope.editContact[i] = false;
    }); 
    $scope.editContact[index] = true;
  }
  $scope.cancelEdit = function(index) {
    if (index == undefined) {
      $scope.createNew = false;
    } else {
      // reset only the one being cancelled
      $scope.contacts[index] = angular.copy($scope.orig[index]);
      $scope.editContact[index] = false;
    }
  }
    
});