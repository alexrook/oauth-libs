'use strict';

/* Controllers */

function getNativeSearchObj() {
    var result = {};

    if (window.location.search) {

        var rsearch = decodeURIComponent(
                window.location.search.substring(1, window.location.search.length));

        angular.forEach(rsearch.split('&'), function (pair) {
            pair = pair.split('=');
            if (pair.length > 1) {
                result[pair[0]] = pair[1];
            } else
                result[pair[0]] = true;
        });

    }

    return result;
}

angular.module('oulApp.controllers', [])
        .controller('RestrCtrl', ['$scope', '$http', '$cookies', function ($scope, $http, $cookies) {
            }])
        .controller('GglCtrl', ['$scope', '$http', '$cookies', function ($scope, $http, $cookies) {

                $scope.STATES = {FIRST: 1, SECOND: 2, THREE: 3, FOUR: 4, ERROR: -1};

                $scope.authId = $cookies['AUTH_ID'];
                console.log($scope.authId);

                $scope.state = $scope.authId ? $scope.STATES.SECOND : $scope.STATES.FIRST;

                $scope.getOnMyServerProfile = function () {
                    var req = {
                        method: 'GET',
                        url: 'oauth/google/profile'
                    };
                    $http(req)
                            .success(function (data) {
                                $scope.state = $scope.STATES.THREE;
                                $scope.profile = data;
                            })
                            .error(function (data, status) {
                                $scope.state = $scope.STATES.ERROR;
                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                            });
                };

                $scope.clearSession = function () {

                    $http
                            .get('oauth/google/logout')
                            .success(function () {
                                delete $cookies['AUTH_ID'];
                                $scope.state = $scope.STATES.FIRST;

                            })
                            .error(function (data, status) {
                                $scope.state = $scope.STATES.ERROR;
                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                            });
                };

            }])
        .controller('MainCtrl', ['$scope', '$location', 'shared',
            function ($scope, $location, shared) {
                console.log(shared);
                $scope.$on('$locationChangeSuccess', function () {
                    shared.host = $location.host();
                    shared.port = $location.port();
                    $scope.host = $location.host();
                    $scope.port = $location.port();
                    $scope.search = angular.toJson($location.search());
                    $scope.searchNative = angular.toJson(getNativeSearchObj());
                    $scope.hash = $location.hash();
                    $scope.path = $location.path();
                });
            }]);
