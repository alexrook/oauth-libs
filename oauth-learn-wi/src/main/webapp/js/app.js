'use strict';

// Declare app level module which depends on filters, and services
angular.module('oulApp', [
    'ngRoute',
    'oulApp.services',
    'oulApp.controllers'
]).config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/authsub', {templateUrl: 'partials/authsub.html', controller: 'AuthSubCtrl'});
        $routeProvider.when('/red', {templateUrl: 'partials/red.html', controller: 'RedCtrl'});
        $routeProvider.otherwise({redirectTo: '/authsub'});
    }]);
