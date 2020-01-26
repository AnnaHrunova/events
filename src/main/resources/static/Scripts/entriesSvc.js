'use strict';
angular.module('eventManagerApp')
    .factory('entriesSvc', ['$http', function ($http) {
        return {
            getEventEntries: function (eventCode) {
                return $http.get('api/entries-event/' + eventCode);
            },
            getClientEntries: function () {
                return $http.get('api/entries-my');
            },
            applyForEntry: function (item) {
                return $http.put('api/entries-apply', item);
            },
            cancelEntry: function (entryCode) {
                return $http.put('api/entries-cancel/' + entryCode);
            },
            acceptEntry: function (entryCode) {
                return $http.put('api/entries-accept/' + entryCode);
            }
        };
    }]);
