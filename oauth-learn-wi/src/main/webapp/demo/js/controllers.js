'use strict';

/* Controllers */

var STATES = {NOTLOGGED: 1, LOGGED: 2, ERROR: -1};

angular.module('todoApp.controllers', [])

        .controller('MainCtrl', ['$scope', '$http', '$cookies',
            function ($scope, $http, $cookies) {


                $scope.authId = $cookies['AUTH_ID'];

                $scope.STATES = STATES;
                $scope.state = STATES.NOTLOGGED;
                $scope.todos = [];
                $scope.currentTodo = {todoId: null};//null here as marker for ui at startup

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

                    var uri = '../oauth/demo/todo/';
                    if ($scope.state === STATES.NOTLOGGED) {
                        uri = uri + 'all';
                    }

                    var req = {
                        method: 'GET',
                        url: uri
                    };

                    return $http(req)
                            .success(function (data) {

                                for (var i = 0; i < data.length; i++) {
                                    data[i].update = new Date(data[i].update).toLocaleString();
                                }
                                $scope.todos = data;


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


                $scope.getTodo = function (todoId) {

                    if ($scope.state === STATES.ERROR)
                        return;

                    var uri = '../oauth/demo/todo/' + todoId;

                    var req = {
                        method: 'GET',
                        url: uri
                    };

                    return $http(req)
                            .success(function (data) {
                                data.update = new Date(data.update).toLocaleString();
                                $scope.currentTodo = data;
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

                $scope.ppdTodo = function (method) {

                    if ($scope.state !== STATES.LOGGED)
                        return;

                    var uri = '../oauth/demo/todo/';

                    var todo = angular.copy($scope.currentTodo);

                    todo.update = $scope.currentTodo.update ?
                            new Date($scope.currentTodo.update) : new Date();

                    console.log($scope.currentTodo.update);

                    if (method === 'DELETE') {
                        uri = uri + todo.todoId;
                    }

                    var req = {
                        method: method,
                        url: uri,
                        data: method === 'DELETE' ? null : todo
                    };

                    return $http(req)
                            .success(function (data) {
                                if (data) {
                                    $scope.currentTodo = data;
                                }
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

                $scope.doLogout = function () {
                    $scope.logout().then(function () {
                        $scope.getList();
                    });
                };

                $scope.doPPD = function (method) {
                    if ($scope.state !== STATES.LOGGED)
                        return;
                    $scope.ppdTodo(method).then(function () {
                        $scope.getList();
                        if ((method === 'DELETE') || (method === 'PUT')) {
                            $scope.currentTodo.todoId = null;
                        }
                    });
                };

                $scope.doEdit = function (todoId) {
                    if ($scope.state !== STATES.LOGGED)
                        return;
                    $scope.getTodo(todoId);
                };

                //console.log($scope.authId);

                if ($scope.authId !== undefined) {
                    $scope.getProfile().then(function () {
                        $scope.getList();
                    });
                } else {
                    $scope.getList();
                }



            }]);
