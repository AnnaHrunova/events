'use strict';
angular.module('eventManagerApp')
  .factory('homeSvc', ['$http', function ($http) {
    return {
      init: function () {
        return $http.get('home');
      }
    };
  }]);
