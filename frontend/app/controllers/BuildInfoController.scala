package controllers

import play.api.mvc._
import play.api.libs.json._
import meta.BuildInfo

object BuildInfoController extends Controller {

  def info = Action {
    Ok(Json parse BuildInfo.toJson)
  }
}
