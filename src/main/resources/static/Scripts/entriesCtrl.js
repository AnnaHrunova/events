'use strict';
angular.module('eventManagerApp')
    .controller('entriesCtrl', ['$scope', '$location', 'entriesSvc', '$routeParams', function ($scope, $location, entriesSvc, $routeParams) {
        $scope.error = '';
        $scope.loadingMessage = '';
        $scope.entriesList = null;

        $scope.applyEntry = {
            entryCode: '',
        };

        $scope.viewSwitch = function (entry) {
            entry.view = !entry.view;
        };

        $scope.applySwitch = function (entry) {
            entry.apply = !entry.apply;
            if (entry.apply) {
              $scope.applyEntry.email = entry.email;
              $scope.applyEntry.code = entry.code;
            }
        };

        $scope.populate = function () {
            var eventCode = $routeParams.eventCode;
            entriesSvc.getEventEntries(eventCode).success(function (results) {
                $scope.entriesList = results;
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };

        $scope.myPopulate = function () {
          entriesSvc.getClientEntries().success(function (results) {
            $scope.entriesList = results;
          }).error(function (err) {
            $scope.error = err;
            $scope.loadingMessage = '';
          })
        };

        $scope.cancelEntry = function (entryCode) {
          entriesSvc.cancelEntry(entryCode).success(function (results) {
                $scope.myPopulate();
                $scope.loadingMessage = results;
                $scope.error = '';
          }).error(function (err) {
            $scope.error = err;
            $scope.loadingMessage = '';
          })
        };

        $scope.acceptEntry = function (entryCode) {
          entriesSvc.acceptEntry(entryCode).success(function (results) {
                $scope.myPopulate();
                $scope.loadingMessage = results;
                $scope.error = '';
          }).error(function (err) {
            $scope.error = err;
            $scope.loadingMessage = '';
          })
        };

        $scope.applyForEntry = function (entry) {
            entriesSvc.applyForEntry(entry).success(function (results) {
                $scope.myPopulate();
                $scope.applySwitch(entry);
                $scope.loadingMessage = results;
                $scope.error = '';
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };
    }]);
