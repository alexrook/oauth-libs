'use strict';


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('oulApp.services', []).
  value('shared', {
      host:'',
      port:''
  });
