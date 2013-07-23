/**
 * Provides both
 *  i) RESTfully backed resources that only do REST (e.g.) InstallationREST
 *  ii) Container classes that make the REST services more easily usable
 *    - E.g. Installation will eagerly load the owning organization on a GET
 *
 * Note that this is written for NG v1.0.6.  Future versions of NG (e.g from v1.2) should support
 * the promise API, simplifying the callbacks.
 *
 * This relies on the following constants:
 *  - INSTALLATION_URL
 *  - ORGANIZATION_URL
 *  - DATASET_URL
 *  - NODE_URL
 *  - DEFAULT_PAGE_SIZE
 *
 * Example config for this might be something like the following in the app:
 *  .constant('NODE_URL', "../node/:key")
 *  .constant('DATASET_URL', "../dataset/:key")
 *  .constant('INSTALLATION_URL', "../installation/:key")
 *  .constant('ORGANIZATION_URL', "../organization/:key")
 *  .constant('DEFAULT_PAGE_SIZE', 1000)
 */

/**
 * A utility base class using prototypical inheritencethat wraps the REST service and provides CRUD
 * Sub classes therefore get common CRUD ops, which can be overridden.  We do this to hide the 
 * annoyance of $resource needing e.g. {key : key} and to provide consistent paging.
 * Note we don't create the $resoruce here, to stop circular dependencies and we need the url 
 * because the backend returns a string, so we can't use resource there.
 *
 * This is tightly coupled to the naming strategy of the state machine.  E.g. given a type of node
 * there should be a node.detail state.  
 *
 * TODO: should NG somehow provide this base class?
 *
 * REST: the service to use
 * url: the url the service is using
 * $http: passed through from NG
 * notifications: the notification service
 * $state: the state provider
 * type: the type to pass through for the state
 */
var _CRUDService = function (REST, url, $http, notifications, $state, type) {
  this.REST = REST;
  this.url = url;
  this.$http = $http;
  this.notifications = notifications;
  this.$state = $state;
  this.type = type;
};

_CRUDService.prototype.get = function (key, success, failure) {
  return this.REST.get({key : key}, success, failure);
};

_CRUDService.prototype.create = function (object) {
  var _notifications = this.notifications;
  var _state = this.$state;
  var _type = this.type;
  // because the service returns String, we need to use http here
  $http.post(this.url, object)
    .success(function(data) { 
      _notifications.pushForNextRoute("Successfully created", 'info');
        // strip the quotes
        _state.transitionTo(_type + '.detail', { key: data.replace(/["]/g,''), type: _type }); 
   })
   .error(function(response) {
     _notifications.pushForCurrentRoute(response, 'error');
   });
}

_CRUDService.prototype.update = function (object) {
  var _notifications = this.notifications;
  var _state = this.$state;
  var _type = this.type;
  this.REST.save(object, 
    function() {
      _notifications.pushForNextRoute("Successully updated", 'info');
      _state.transitionTo(_type + '.detail', { key: object.key, type: _type }); 
    },
    function(response) {
      _notifications.notifications.pushForCurrentRoute(response.data, 'error');
    }  
  );
};

_CRUDService.prototype.delete = function (object) {
  var _notifications = this.notifications;
  var _state = this.$state;
  var _type = this.type;
  this.REST.delete(object,
      function() {
        _notificationspushForNextRoute("Successfully deleted", 'info');
        _state.transitionTo(_type + '.detail', { key: object.key, type: _type });
      },
      function(response) {
        this.notifications.pushForCurrentRoute(response.data, 'error');
      }        
    );
}

_CRUDService.prototype.query = function (q, callback) {
  $http.get(this.url + '?q=' + q).success(calback);
}


/**
 * Using the provided REST service, will call a GET for the key (if present, otherwise does nothing) 
 * and puts the response as a named parameter on the target object.  Thus it can simply populate 
 * sub resource.  For example the following might be used to load the 'organization' property on the 
 * installation:
 *   _eagerlyLoad(
 *     OrganizationREST, installation.organizationKey, installation, 'organization');   
 *
 * REST: the service to use
 * key: to call the get with on REST (may be null in which case nothing happens)
 * target: to populate
 * property: to put the name 
 */
// eagerly loads the keyed organization using the given REST service into the target if the key is 
// present
var _eagerlyLoad = function(REST, key, target, property) {
  if (key) {
    REST.get({key : key}, function(result) {
      target[property] = result;
    });
  }  
}
 
angular.module('resources', ['ngResource'])
 // Vanilla REST services
.factory('InstallationREST', function ($resource, INSTALLATION_URL) {
  return $resource(INSTALLATION_URL + "/:key", {key : '@key'}, { save : {method:'PUT'} })
  })
.factory('OrganizationREST', function ($resource, ORGANIZATION_URL) {
  return $resource(ORGANIZATION_URL + "/:key", {key : '@key'}, { save : {method:'PUT'} })
  })
.factory('NodeREST', function ($resource, NODE_URL) {
  return $resource(NODE_URL + "/:key", {key : '@key'}, { save : {method:'PUT'} })
  })
.factory('DatasetREST', function ($resource, DATASET_URL) {
  return $resource(DATASET_URL + "/:key", {key : '@key'}, { save : {method:'PUT'} })
  })
        

/**
 * Eagerly loading installation manager
 */
.factory('InstallationManager', function (InstallationREST, OrganizationREST, INSTALLATION_URL, $http, $state, notifications) {
  var Service = function () {};  
  Service.prototype = new _CRUDService(InstallationREST, INSTALLATION_URL, $http, notifications, $state, "installation");
	
  Service.prototype.get = function (key) {
    return _CRUDService.prototype.get.call(this, key, function(installation) {  
      _eagerlyLoad(OrganizationREST, installation.organizationKey, installation, 'organization');
    })
  };
  
  return new Service();
})

.factory('NodeManager', function (NodeREST, NODE_URL, $http, $state, notifications) {
  var Service = function () {};  
  Service.prototype = new _CRUDService(NodeREST, NODE_URL, $http, notifications, $state, "node");
  
  Service.prototype.get = function (key) {
    return _CRUDService.prototype.get.call(this, key, function(node) {  
      var count = function(url, parameter) {
      $http( { method:'GET', url: url})
        .success(function (result) {
          node[parameter] = result.results;
          node[parameter + "Count"] = result.count;
        });
    }
    count(NODE_URL + "/" +  key + '/dataset','datasets');
    count(NODE_URL + "/" +  key + '/pendingEndorsement','pendingEndorsements');
      
    })
  };
  
  Service.prototype.pending = function (key, callback) {
    $http( { method:'GET', url: NODE_URL + "/" +  key + '/pendingEndorsement'}).success(callback);
  }
  
  
  return new Service();
  
})


;
  
