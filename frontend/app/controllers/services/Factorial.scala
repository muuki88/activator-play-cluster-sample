package controllers.services

import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current

import actors.services.FactorialWebsocketActor
import actors.services.FactorialWebsocketActor._
import api.FactorialService

object Factorial extends Controller {
  
  def websocket() = WebSocket.acceptWithActor[FactorialService.Compute, FactorialService.Result] { implicit request =>
    FactorialWebsocketActor.props(_)
  }
  
}