/**
 * Dashboard controllers.
 */
define(['underscore'], function() {
  'use strict';

  var DashboardCtrl = function($scope, playRoutes) {
    /** Creating the websocket to watch for cluster node changes */
    var websocketUrl = playRoutes.controllers.Monitor.websocket().webSocketUrl();
    var ws = new WebSocket(websocketUrl);
    
    /** Contains all the nodes */
    var nodes = {};
    
    /**
     * This could be refactored into a nice angular service,
     * but for the sake of simplicity, we put it all in here
     */
    ws.onmessage = function(msg) {
      var node = JSON.parse(msg.data);
      if(node.state == 'removed') {
        delete nodes[node.address];
      } else {
        nodes[node.address] = node;
      }
      $scope.$apply(function(){
        $scope.nodes = _.values(nodes);
      });
    };
  };
  DashboardCtrl.$inject = [ '$scope', 'playRoutes' ];

  return {
    DashboardCtrl : DashboardCtrl
  };

});
