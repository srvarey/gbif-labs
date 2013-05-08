/**
 * A helper that will provide a simple wrapper to perform crud on $resource objects.
 */
angular.module('services.crud').factory('crudEditMethods', function () {

  return function (itemName, item, formName, successcb, errorcb) {