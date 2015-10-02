package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.Play.current
import actors._
import actors.MetricsActor._
import models.Metrics

object Cluster extends Controller {

  def clusterNodesWebsocket = WebSocket.acceptWithActor[JsValue, JsValue] { implicit request =>
    MonitorActor.props
  }
  
  def clusterMetricsWebsocket = WebSocket.acceptWithActor[JsValue, Metrics.NodeMetric] { implicit request =>
    MetricsActor.props
  }
}