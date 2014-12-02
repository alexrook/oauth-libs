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
        .controller('MainCtrl', ['$scope', '$location', function ($scope, $location) {
                console.log($location.search());
                console.log($location.absUrl());
                console.log($location.hash());
            }]);
