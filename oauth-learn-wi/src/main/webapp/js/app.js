'use strict';

// Declare app level module which depends on filters, and services
angular.module('oulApp', [
    'ngRoute',
    'oulApp.services',
    'oulApp.controllers'
]).config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/authsub', {templateUrl: 'partials/authsub.html', controller: 'AuthSubCtrl'});
        $routeProvider.when('/view2', {templateUrl: 'partials/partial2.html', controller: 'MyCtrl2'});
        $routeProvider.otherwise({redirectTo: '/authsub'});
    }]);
