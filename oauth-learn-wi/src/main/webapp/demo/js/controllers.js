'use strict';

/* Controllers */

/*       {
 "id": "1234567890",
 "domain": "",
 "imgUri": "http://---",
 "emails": [
 "user@example.com"
 ],
 "name": {
 "firstName": "User",
 "lastName": "Ex",
 "displayName": "User Ex"
 }
 }*/

var STATES = {LOGGED: 1, NOTLOGGED: 2, ERROR: -1};

angular.module('todoApp.controllers', [])

        .controller('MainCtrl', ['$scope', '$http', '$cookies',
            function ($scope, $http, $cookies) {

                $scope.authId = $cookies['AUTH_ID'];

                $scope.STATES = STATES;
                $scope.state = STATES.NOTLOGGED;

                $scope.getProfile = function () {

                    var req = {
                        method: 'GET',
                        url: '../oauth/google/profile'
                    };

                    $http(req)
                            .success(function (data) {
                                $scope.profile = data;
                                $scope.state = STATES.LOGGED;
                            })
                            .error(function (data, status) {
                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                                $scope.state = STATES.ERROR;
                                console.log($scope.err);
                            });
                };

                $scope.logout = function () {
                    $http
                            .get('../oauth/google/logout')
                            .success(function () {
                                delete $cookies['AUTH_ID'];
                                $scope.authId = null;
                                $scope.profile = null;
                                $scope.state = STATES.NOTLOGGED;
                            })
                            .error(function (data, status) {

                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                                $scope.state = STATES.ERROR;
                                console.log($scope.err);
                            });
                };

                if ($scope.authId!==undefined) {
                    $scope.getProfile();
                }

            }]);
