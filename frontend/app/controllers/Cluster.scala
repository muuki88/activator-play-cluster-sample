package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.Play.current
import actors._
import actors.MetricsActor._

object Cluster extends Controller {

  def clusterNodesWebsocket = WebSocket.acceptWithActor[JsValue, JsValue] { implicit request =>
    MonitorActor.props(_)
  }
  
  def clusterMetricsWebsocket = WebSocket.acceptWithActor[JsValue, MetricsActor.NodeMetric] { implicit request =>
    MetricsActor.props(_)
  }
}