/**
 * Defines a directive for inserting a simple bar
 * chart with d3.
 * 
 * @see http://cloudspace.com/blog/2014/03/25/creating-d3.js-charts-using-angularjs-directives/#.U8JnkDdJXo9
 */
define(['angular', 'd3'], function(angular, d3) {
  'use strict';

  var mod = angular.module('dashboard.directives.barchart', []);
  mod.directive('barchart', function() {
    return {
      restrict: 'E',
      scope : {
        data : '='
      },
      link: function(scope, element) {
        
        // setting height/width of the svg
        var margin = {top: 20, right: 0, bottom: 30, left: 50},
          width = element.width() - margin.left - margin.right,
          height = element.height() - margin.top - margin.bottom;
        
        // bootstrap layout sets height to 1
        if(height < (margin.top + margin.bottom)) {
          height = 300 - margin.top - margin.bottom;
        }
        
        var format = d3.format('s');
        
        var svg = d3.select(element[0])
          .append('svg')
          .attr('class', 'barchart')
          .attr('width', width  + margin.left + margin.right)
          .attr('height', height  + margin.top + margin.bottom);
        
        var x = d3.scale.ordinal().rangeRoundBands([0, width], 0.1);
        var y = d3.scale.linear().range([height, 0]);
        
        var xAxis = d3.svg.axis()
          .scale(x)
          .orient('bottom');
        
        var yAxis = d3.svg.axis()
          .scale(y)
          .tickFormat(format)
          .orient('left');
        
        var color = d3.scale.linear()
          .range(['#4B8A08', '#FE2E2E']);
        
        //Render graph based on 'data'
        scope.render = function(data) {
          
          x.domain(data.map(function(d) { return d.name; }));
          y.domain([0, d3.max(data, function(d) { return d.value; })]);
          
          color.domain([0, d3.max(data, function(d) { return d.value; })]);
            
          
          // redraw all axes
          svg.selectAll('g.axis').remove();
          
          svg.append('g')
            .attr('class', 'x axis')
            .attr('transform', 'translate(' + margin.left + ',' + height + ')')
            .call(xAxis);
          
          svg.append('g')
            .attr('class', 'y axis')
            .attr('transform', 'translate(' + margin.left + ',0)')
            .call(yAxis);
          
          
          // finally the bars
          var bar = svg.selectAll('g.bar')
            .data(data);
          
          var barEnter = bar
            .enter().append('g')
            .attr('class', 'bar');
          
          // UPDATE
          bar
            .select('rect')
            .attr('transform', function(d) { return 'translate(' + (x(d.name) + margin.left) + ',0)'; })
            .attr('y', function(d) { return y(d.value); })
            .attr('height', function(d) { return height - y(d.value); })
            .attr('width', x.rangeBand())
            .attr('fill', function(d) { return color(d.value); });
          
          // CREATE
          barEnter.append('rect')
            .attr('transform', function(d) { return 'translate(' + (x(d.name) + margin.left) + ',0)'; })
            .attr('class', 'bar')
            .attr('y', function(d) { return y(d.value); })
            .attr('height', function(d) { return height - y(d.value); })
            .attr('width', x.rangeBand())
            .attr('fill', function(d) { return color(d.value); });
          
          // DELETE
          bar.exit().remove();

        };
        
        //Watch 'data' and run scope.render(newVal) whenever it changes
        //Use true for 'objectEquality' property so comparisons are done on equality and not reference
        scope.$watch('data', function(){
          scope.render(scope.data);
        }, true);  
        
      }
    };
  });
  
  return mod;
});