package controllers.services

import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current

import actors.services.FactorialActor
import actors.services.FactorialActor._
import api.FactorialService

object Factorial extends Controller {
  
  def websocket() = WebSocket.acceptWithActor[FactorialService.Compute, FactorialService.Result] { implicit request =>
    FactorialActor.props(_)
  }
  
}