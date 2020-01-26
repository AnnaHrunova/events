'use strict';
angular.module('eventManagerApp')
    .controller('homeCtrl', ['$scope', '$location', 'homeSvc', '$routeParams', function ($scope, $location, homeSvc, $routeParams) {
        $scope.error = '';
        $scope.loadingMessage = '';
        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
        $scope.populate = function () {
          homeSvc.init('').success(function (results) {
          }).error(function (err) {
            $scope.error = err;
            $scope.loadingMessage = '';
          })
        };
    }]);
