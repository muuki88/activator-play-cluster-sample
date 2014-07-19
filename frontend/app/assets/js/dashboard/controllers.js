/**
 * Dashboard controllers.
 */
define([ 'underscore' ], function() {
  'use strict';

  var DashboardCtrl = function($scope, playRoutes) {
    $scope.metrics = [];
    $scope.nodes = [];

    /** Creating the websocket to watch for cluster node changes */
    var clusterNodesSocketUrl = playRoutes.controllers.Cluster.clusterNodesWebsocket().webSocketUrl();
    var clusterNodesWS = new WebSocket(clusterNodesSocketUrl);

    /** Contains all the nodes */
    var nodes = {};

    /**
     * This could be refactored into a nice angular service, but for the sake of
     * simplicity, we put it all in here
     */
    clusterNodesWS.onmessage = function(msg) {
      var node = JSON.parse(msg.data);
      if (node.state == 'removed') {
        delete nodes[node.address];
      } else {
        nodes[node.address] = node;
      }
      $scope.$apply(function() {
        $scope.nodes = _.values(nodes);
      });
    };

    // clusterMetricsWebsocket
    var clusterMetricsSocketUrl = playRoutes.controllers.Cluster.clusterMetricsWebsocket().webSocketUrl();
    var clusterMetricsWS = new WebSocket(clusterMetricsSocketUrl);

    clusterMetricsWS.onmessage = function(msg) {
      var metric = JSON.parse(msg.data);

      $scope.$apply(function() {
        var node = nodes[metric.address.full];
        if (node === undefined) {
          // node not available
        } else if (metric.name == 'heap') {
          node.heap = (metric.used / (1024 * 1024)).toFixed(2) + 'Mb';
          node.heapUsed = metric.used;
        } else if (metric.name == 'cpu') {
          if (metric.cpuCombined) {
            node.cpu = (metric.cpuCombined * 100).toFixed(2);
            node.cpuCombined = metric.cpuCombined;
          }
          node.systemLoadAverage = metric.systemLoadAverage;
        }

        // Setting metrics
        $scope.metrics = _.values(nodes).map(function(node) {
          return {
            name : node.host + ':' + node.port,
            value : (node.heapUsed || 0)
          };
        });

      });
    };
  };
  DashboardCtrl.$inject = [ '$scope', 'playRoutes' ];

  return {
    DashboardCtrl : DashboardCtrl
  };

});
