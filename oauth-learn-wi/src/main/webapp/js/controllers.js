'use strict';

/* Controllers */

function getNativeSearchObj() {
    var result={};
    
    if (window.location.search) {
	var rsearch=window.location.search.substring(1,window.location.search.length);
	
	angular.forEach(rsearch.split('&'),function(pair){
		pair=pair.split('=');
		if (pair.length>1) {
		    result[pair[0]]=pair[1];
		}else
		    result[pair[0]]=true;
	    });
	
    }
    
    return result;
}

angular.module('oulApp.controllers', [])
        .controller('AuthSubCtrl', ['$scope', '$routeParams', function ($scope, $routeParams) {
		$scope.authSub2Call=function(){
		    
		}
            }])
        .controller('MyCtrl2', ['$scope', function ($scope) {

            }])
        .controller('MainCtrl', ['$scope','$location',
		function ($scope, $location) {
                $scope.host=$location.host();
		$scope.port=$location.port();
		$scope.search=angular.toJson($location.search());
		$scope.searchNative=angular.toJson(getNativeSearchObj());
		$scope.hash=$location.hash();
		$scope.path=$location.path();
            }]);
