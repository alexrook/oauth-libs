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
        .controller('AuthSubCtrl', ['$scope', '$http', function ($scope, $http) {

                $scope.STATES = {FIRST: 1, SECOND: 2, THREE: 3, ERROR: -1};
                $scope.state = $scope.STATES.FIRST;
                $scope.authsub = {
                    slsuToken: (getNativeSearchObj().token ? getNativeSearchObj().token : '')
                };
                if ($scope.authsub.slsuToken) {
                    $scope.state = $scope.STATES.SECOND;
                }

                $scope.authSub2Call = function () {

                    var req = {
                        method: 'GET',
                        url: 'http://devhelper:8080/oul/rest/proxy',
                        params: {
                            uri: 'https://google.com/accounts/AuthSubSessionToken'
                        },
                        headers: {
                            'X-Proxy-This': 'Authorization=' + 'AuthSub token=' + $scope.authsub.slsuToken
                        }

                    };

                    $http(req)
                            .success(function (data, status) {
                                console.log(data);
                                $scope.sessionToken = data;
                                $scope.state = $scope.STATES.THREE;
                            })
                            .error(function (data, status) {
                                console.log(data);
                                $scope.err = {
                                    data: data,
                                    status: status
                                };
                                $scope.state = $scope.STATES.ERROR;
                            });
                };
            }])
        .controller('MyCtrl2', ['$scope', function ($scope) {

            }])
        .controller('MainCtrl', ['$scope', '$location',
            function ($scope, $location) {
                $scope.host = $location.host();
                $scope.port = $location.port();
                $scope.search = angular.toJson($location.search());
                $scope.searchNative = angular.toJson(getNativeSearchObj());
                $scope.hash = $location.hash();
                $scope.path = $location.path();
            }]);
