'use strict';

angular.module('oulApp', [
    'ngRoute',
    'ngCookies',
    'oulApp.services',
    'oulApp.controllers'
]).config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/google-oauth', {templateUrl: 'partials/google-oauth.html', controller: 'GglCtrl'});
        $routeProvider.when('/restricted', {templateUrl: 'partials/restricted.html', controller: 'RestrCtrl'});
        $routeProvider.otherwise({redirectTo: '/google-oauth'});
    }]);
