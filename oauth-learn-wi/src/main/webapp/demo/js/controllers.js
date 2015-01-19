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

angular.module('todoApp.controllers', [])

        .controller('MainCtrl', ['$scope', '$http', '$cookies',
            function ($scope, $http, $cookies) {

                $scope.STATES = {LOGGED: 1, NOTLOGGED: 2, ERROR: -1};

                $scope.authId = $cookies['AUTH_ID'];

                $scope.getProfile = function () {

                    var req = {
                        method: 'GET',
                        url: '../oauth/google/profile'
                    };

                    $http(req)
                            .success(function (data) {
                                $scope.profile = data;
                                $scope.state = $scope.STATES.LOGGED;
                            })
                            .error(function (data, status) {
                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                                $scope.state = $scope.STATES.ERROR;
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
                                $scope.state = $scope.STATES.NOTLOGGED;
                            })
                            .error(function (data, status) {

                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                                $scope.state = $scope.STATES.ERROR;
                                console.log($scope.err);
                            });
                };

                if ($scope.authId) {
                    $scope.getProfile();
                }

            }]);
