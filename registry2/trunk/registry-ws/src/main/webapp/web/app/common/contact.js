angular.module('contact', ['services.notifications'])

.controller('ContactCtrl', function ($scope, $state, $stateParams, notifications, Restangular) {
	var type = $state.current.context; // this context should be set in the parent statemachine (e.g. dataset)
	var key = $stateParams.key; // the entity key (e.g. uuid of a dataset)
	
	var resetState = function() {
	  $state.transitionTo(type + '.contact', { key: key}); 
	}

  // help provide context with a label to the user
  var typeLabel = $state.current.context;
  $scope.typeLabel = typeLabel.charAt(0).toUpperCase() + typeLabel.slice(1);
  
  var contacts = Restangular.one(type, key).all('contact');
  contacts.getList().then(function(response) {$scope.contacts = response});
  
  $scope.types = [
    'ADMINISTRATIVE',
    'TECHNICAL'
  ];	

  // the parent entity has a nested set, which shows as the count in the menu
  // this updates it
	var updateParent = function() {
    $scope[type].contacts.length=0;
    $.each($scope.contacts, function(index, item) {
      $scope[type].contacts[index] = item;
    });
	}

  $scope.save = function(item) {
    var success = function(data) {
      notifications.pushForCurrentRoute("Contact successfully updated", 'info');
      $scope.close();
      if (!item.key) {
        contacts.getList().then(function(response) {$scope.contacts = response});
        $scope.counts.contact++;
        resetState(); // in case we have logged in
      }
    };
    
    var failure = function(response) {
      notifications.pushForCurrentRoute(response.data, 'error');
    };  
  
    if (item.key != null) {
      var ngItem = _.find($scope.contacts, function(i) {
        return item.key == i.key;
      });
      ngItem.put().then(success,failure);
    } else {
      Restangular.one(type, key).all("contact").post(item).then(success,failure);
    }
    
  }
  
  $scope.delete = function(item) {
    var ngItem = _.find($scope.contacts, function(i) {
      return item.key == i.key;
    });
    ngItem.remove().then(
      function() {
        notifications.pushForCurrentRoute("Contact successfully deleted", 'info');
        $scope.contacts = _.without($scope.contacts, ngItem);
        $scope.counts.contact--;
        $scope.close();
        resetState(); // in case we have logged in
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });
  }
  	 
  $scope.close = function() {
    $scope.createNew=false;
    $.each($scope.contacts, function(i,contact) {
      contact.editMode = false;
    });    
  }
});