'use strict';

// Declare app level module which depends on filters, and services
angular.module('oulApp', [
    'ngRoute',
    'ngCookies',
    'oulApp.services',
    'oulApp.controllers'
]).config(['$routeProvider','$cookiesProvider', function ($routeProvider,$cookiesProvider) {
        $routeProvider.when('/authsub', {templateUrl: 'partials/authsub.html', controller: 'AuthSubCtrl'});
        $routeProvider.when('/red', {templateUrl: 'partials/red.html', controller: 'RedCtrl'});
        $routeProvider.when('/google-oauth', {templateUrl: 'partials/google-oauth.html', controller: 'GglCtrl'});
        $routeProvider.otherwise({redirectTo: '/authsub'});
    }]);
