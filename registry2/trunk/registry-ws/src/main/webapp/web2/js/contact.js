var contactApp = angular.module('registry.contact', ['ngResource']);

// RESTfully backed Node resource 
contactApp.factory('Contact', function() {
  return {"title" : "tim"};
});

contactApp.controller('ContactListController', function($scope, Contact) {
  $scope.contact = new Contact();
});