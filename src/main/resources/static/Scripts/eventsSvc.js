'use strict';
angular.module('eventManagerApp')
    .factory('eventsSvc', ['$http', function ($http) {
        return {
            getEvents: function () {
                return $http.get('api/events');
            },
            getClientEvents: function () {
                return $http.get('api/events-my');
            },
            addEvent: function (item) {
                return $http.post('api/events/', item);
            },
            updateDescription: function (item) {
                return $http.put('api/events/', item);
            },
        };
    }]);
