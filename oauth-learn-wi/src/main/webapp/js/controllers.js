'use strict';

/* Controllers */

angular.module('oulApp.controllers', [])
        .controller('AuthSubCtrl', ['$scope', '$routeParams', function ($scope, $routeParams) {
            console.log($routeParams);
               // console.log($location.absUrl());
               // console.log($location.hash());
            }])
        .controller('MyCtrl2', ['$scope', function ($scope) {

            }])
        .controller('MainCtrl', ['$scope','$location',
		function ($scope, $location) {
                $scope.host=$location.host();
		$scope.port=$location.port();
		$scope.search=angular.toJson($location.search());
		$scope.searchNative=angular.toJson(window.location.search);
		$scope.hash=$location.hash();
		$scope.path=$location.path();
            }]);
