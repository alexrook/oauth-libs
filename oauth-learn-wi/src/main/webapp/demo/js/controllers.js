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

var STATES = {NOTLOGGED: 1, LOGGED: 2, ERROR: -1};


angular.module('todoApp.controllers', [])

        .controller('MainCtrl', ['$scope', '$http', '$cookies',
            function ($scope, $http, $cookies) {

                $scope.authId = $cookies['AUTH_ID'];

                $scope.STATES = STATES;
                $scope.state = STATES.NOTLOGGED;
                $scope.todos = [];

                $scope.getProfile = function () {

                    if ($scope.state === STATES.ERROR)
                        return;

                    var req = {
                        method: 'GET',
                        url: '../oauth/google/profile'
                    };

                    return $http(req)
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
                    return  $http
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

                            });
                };


                $scope.getList = function () {

                    if ($scope.state === STATES.ERROR)
                        return;

                    var uri = '../oauth/demo/todo';
                    if ($scope.state === STATES.NOTLOGGED) {
                        uri = uri + '/all';
                    }
                    return $http
                            .get(uri)
                            .success(function (data) {
                                $scope.todos = data.todos ? data.todos : data;
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

                console.log($scope.authId);
                if ($scope.authId !== undefined) {
                    $scope.getProfile().then(function () {
                        $scope.getList();
                    });
                } else {
                    $scope.getList();
                }



                console.log($scope.state);
            }]);
