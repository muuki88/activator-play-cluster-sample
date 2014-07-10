package actors.services

import akka.actor._
import akka.routing.FromConfig

import play.api.libs.json._
import play.api.mvc.WebSocket.FrameFormatter

import scala.concurrent.duration._
import api.FactorialService._

/**
 * Websocket actor which will be instantiated by the
 * playframework on a websocket connection.
 *
 * Forwards calculation to the cluster andreturns results to the websocket
 */
class FactorialActor(out: ActorRef) extends Actor with ActorLogging {

  /**
   * Selects the local factorialService actor.
   * This actor submits jobs into the cluster.
   */
  val service: ActorSelection = {
    val path = context.system / "factorialService"
    context actorSelection path
  }

  def receive = {
    case calc: Compute  => service ! calc // calculate it
    case result: Result => out ! result // promote result
  }

}

/**
 * Contains props and formats
 */
object FactorialActor {

  /**
   * How to create the websocket actor
   */
  def props(out: ActorRef) = Props(classOf[FactorialActor], out)

  // BigInt conversions
  val bigIntReads: Reads[BigInt] = Reads[BigInt] {
    n => JsSuccess(BigInt(n.as[String]))
  }

  val bigIntWrites: Writes[BigInt] = Writes {
    n => JsString(n.toString)
  }

  implicit val bigIntFormat: Format[BigInt] = Format(bigIntReads, bigIntWrites)

  // api conversions
  implicit val resultFormat = Json.format[Result]
  implicit val resultFormatter = FrameFormatter.jsonFrame[Result]

  implicit val computeFormat = Json.format[Compute]
  implicit val computeFormatter = FrameFormatter.jsonFrame[Compute]
}