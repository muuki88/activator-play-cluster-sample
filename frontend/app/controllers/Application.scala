package controllers

import models._

import play.api._
import play.api.mvc._
import play.api.cache._
import play.api.Play.current

/** Application controller, handles authentication */
object Application extends Controller {

  /** Serves the index page, see views/index.scala.html */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * Returns the JavaScript router that the client can use for "type-safe" routes.
   * Uses browser caching; set duration (in seconds) according to your release cycle.
   * @param varName The name of the global variable, defaults to `jsRoutes`
   */
  def jsRoutes(varName: String = "jsRoutes") = Cached(_ => "jsRoutes", duration = 86400) {
    Action { implicit request =>
      Ok(
        Routes.javascriptRouter(varName)(
            routes.javascript.Cluster.clusterNodesWebsocket,
            routes.javascript.Cluster.clusterMetricsWebsocket,
            services.routes.javascript.Factorial.websocket
          // TODO Add your routes here
        )
      ).as(JAVASCRIPT)
    }
  }


}
