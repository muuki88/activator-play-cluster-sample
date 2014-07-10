/**
 * Services controllers.
 */
define([ 'underscore' ], function() {
  'use strict';

  var ServicesCtrl = function($scope, playRoutes) {

  };
  ServicesCtrl.$inject = [ '$scope', 'playRoutes' ];

  var FactorialCtrl = function($scope, playRoutes) {
    /** Creating the websocket to watch for cluster node changes */
    var websocketUrl = playRoutes.controllers.services.Factorial.websocket().webSocketUrl();
    var ws = new WebSocket(websocketUrl);

    /**
     * This could be refactored into a nice angular service, but for the sake of
     * simplicity, we put it all in here
     */
    ws.onmessage = function(msg) {
      var data = JSON.parse(msg.data);
      $scope.$apply(function(){
        var result = data.result;
        if(data.result.length > 10) {
          result = data.result.substr(1,7) + '...';
        }
        
        $scope.result = result;
        $scope.done = $scope.done + 1;
        if($scope.done == $scope.times) {
           $scope.finished = new Date();
           $scope.runtime = ($scope.finished.getTime() - $scope.start.getTime()) / 1000;
        }
      });
    };
    
    /**
     * Starting n-times calculations
     */
    $scope.startCalculation = function() {
      $scope.done = null;
      $scope.result = null;
      $scope.runtime = null;
      $scope.start = new Date();
      var times = $scope.times;
      var fac = $scope.factorial;
      for(var i = 0; i < times; i++) {
        ws.send(JSON.stringify({
          n : fac
        }));
      }
    };

  };
  FactorialCtrl.$inject = [ '$scope', 'playRoutes' ];

  return {
    FactorialCtrl : FactorialCtrl
  };

});
