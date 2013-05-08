/**
 * RESTfully backed Node resource  
 */
angular.module('resources.node', ['ngResource']);
angular.module('resources.node').factory('Node', function ($resource, $q) {
  var Node = $resource('../node/:nodeKey', {nodeKey : '@key'}, {
    save : {method:'PUT'}
  });  
  
  // A synchronous get, with a failure callback on error
  Node.getSync = function (key, failureCb) {
    var deferred = $q.defer();
    Node.get({nodeKey: key}, function(successData) {
      deferred.resolve(successData); 
    }, function(errorData) {
      deferred.reject(); // you could optionally pass error data here
      if (failureCb) {
        failureCb();
      }
    });
    return deferred.promise;
  };
  
  Node.prototype.ping = function() {
    alert("ping");
  };
   
  
  return Node;
});
