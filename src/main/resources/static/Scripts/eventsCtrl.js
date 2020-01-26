'use strict';
angular.module('eventManagerApp')
    .controller('eventsCtrl', ['$scope', '$location', 'eventsSvc', function ($scope, $location, eventsSvc) {
        $scope.error = '';
        $scope.loadingMessage = '';
        $scope.eventsList = null;
        $scope.editingInProgress = false;
        $scope.newEventCaption = '';

        $scope.editEvent = {
            description: '',

        };

        $scope.editSwitch = function (event) {
            event.edit = !event.edit;
            if (event.edit) {
                $scope.editEvent.description = event.description;
                $scope.editingInProgress = true;
            } else {
                $scope.editingInProgress = false;
            }
        };

        $scope.viewSwitch = function (event) {
            event.view = !event.view;
        };

        $scope.populate = function () {
            eventsSvc.getEvents()
            .success(function (results) {
                $scope.eventsList = results;
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };

        $scope.updateDescription = function (event) {
            eventsSvc.updateDescription($scope.editEvent)
            .success(function (results) {
                $scope.populate();
                $scope.editSwitch(event);
                $scope.loadingMessage = results;
                $scope.error = '';
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMessage = '';
            })
        };

        $scope.addEvent = function () {
            eventsSvc.addEvent({
                'name': $scope.newEventCaption,
            }).success(function (results) {
                $scope.newEventCaption = '';
                $scope.myPopulate();
                $scope.loadingMessage = results;
                $scope.error = '';
            }).error(function (err) {
                $scope.error = err;
                $scope.loadingMsg = '';
            })
        };

        $scope.myPopulate = function () {
          eventsSvc.getClientEvents().success(function (results) {
            $scope.eventsList = results;
          }).error(function (err) {
            $scope.error = err;
            $scope.loadingMessage = '';
          })
        };

        $scope.redirectToEntries = function(eventCode){
          window.location = "#/Entries/" + eventCode;
        }
    }]);
