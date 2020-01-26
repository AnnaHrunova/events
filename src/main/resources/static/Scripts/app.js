'use strict';
angular.module('eventManagerApp', ['ngRoute'])
    .config(['$routeProvider',  function ($routeProvider) {
        $routeProvider.when('/Home', {
          controller: 'homeCtrl',
          templateUrl: 'Views/Home.html'
        }).when('/Events', {
            controller: 'eventsCtrl',
            templateUrl: 'Views/EventsList.html'
        }).when('/Entries/:eventCode', {
            controller: 'entriesCtrl',
            templateUrl: 'Views/EntriesList.html'
        }).when('/MyEntries', {
            controller: 'entriesCtrl',
            templateUrl: 'Views/MyEntries.html'
          }).when('/MyEvents', {
          controller: 'eventsCtrl',
          templateUrl: 'Views/MyEvents.html'
        })
        .otherwise({redirectTo: '/Home'});
    }]);
