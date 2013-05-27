angular.module('comment', ['services.notifications'])

.controller('CommentCtrl', function ($scope, $state, $stateParams, $resource, notifications) {
  var Comment = $resource('../:type/:key/comment/:commentKey', {
    type : $state.current.context, // this context should be set in the parent statemachine (e.g. node)
    key : $stateParams.key,  
    commentKey : '@id'}
  );
  
  // loads the comments, and updates the scope
  var refreshScope = function() {
    Comment.query(function(data) {
      $scope.comments = data;
      $scope.counts.comment = data.length; // update parent counts
    });
  }
  
  refreshScope();

  $scope.save = function(item) {
    item.createdBy = "TODO: security for comment.js";
    item.modifiedBy = "TODO: security for comment.js";
    Comment.save(item,
      function() {
        notifications.pushForCurrentRoute("Comment successfully updated", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });  
  }
  
  $scope.delete = function(comment) {
    Comment.delete({commentKey : comment.key},
      function() {
        notifications.pushForCurrentRoute("Comment successfully deleted", 'info');
        refreshScope();
        $scope.editing = false; // close the form
      },
      function(response) {
        notifications.pushForCurrentRoute(response.data, 'error');
      });
  }
});