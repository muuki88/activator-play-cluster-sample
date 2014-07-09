/**
 * Dashboard routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('dashboard.routes', ['yourprefix.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',  {templateUrl: '/assets/partials/dashboard/dashboard.html',  controller:controllers.DashboardCtrl});
  }]);
  return mod;
});
