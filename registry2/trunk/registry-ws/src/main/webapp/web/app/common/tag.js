angular.module('tag', ['services.notifications'])

.controller('TagCtrl', function ($scope, $state, $stateParams, $resource, notifications) {
  // help provide context with a label to the user
  var typeLabel = $state.current.context;
  $scope.typeLabel = typeLabel.charAt(0).toUpperCase() + typeLabel.slice(1);

  var Tag = $resource('../:type/:key/tag/:tagKey', {
    type : $state.current.context, // this context should be set in the parent statemachine (e.g. node)
    key : $stateParams.key,  
    tagKey : '@id'}
  );
  
  // loads the tags, and updates the scope
  var refreshScope = function() {
    Tag.query(function(data) {
      $scope.tags = data;
      $scope.counts.tag = data.length; // update parent counts
    });
  }
  
  refreshScope();

  $scope.save = function(item) {
    item.createdBy = "TODO: security for tag.js";
    Tag.save(item.value, // it is just a string push
      function() {
        notifications.pushForCurrentRoute("Tag successfully updated", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  }
  
  $scope.delete = function(tag) {
    Tag.delete({tagKey : tag.key},
      function() {
        notifications.pushForCurrentRoute("Tag successfully deleted", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  
  }
});