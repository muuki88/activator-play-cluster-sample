package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.Play.current
import actors.MonitorActor

object Monitor extends Controller {

  def websocket = WebSocket.acceptWithActor[JsValue, JsValue] { implicit request =>
    MonitorActor.props(_)
  }
}