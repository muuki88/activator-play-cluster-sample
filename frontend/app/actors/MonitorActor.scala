package actors

import akka.actor._
import akka.cluster._
import akka.cluster.ClusterEvent._
import play.api.libs.json._
import play.api.libs.functional.syntax._

class MonitorActor(out: ActorRef) extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  /**
   * Converts a cluster member to a json object
   */
  implicit val memberWrites: Writes[Member] = Writes { member =>
    Json.obj(
      "system" -> member.address.system,
      "host" -> member.address.host,
      "port" -> member.address.port,
      "protocol" -> member.address.protocol,
      "address" -> member.address.toString,
      "roles" -> member.roles
    )
  }

  // subscribe to cluster changes, re-subscribe when restart 
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member)                      => handleMemberUp(member)
    case UnreachableMember(member)             => handleUnreachable(member)
    case MemberRemoved(member, previousStatus) => handleRemoved(member, previousStatus)
    case MemberExited(member)                  => handleExit(member)
    case _: MemberEvent                        => // ignore
  }

  def handleMemberUp(member: Member) {
    log info s"Member is Up: ${member.address}"
    out ! (Json.obj("state" -> "up") ++ Json.toJson(member).as[JsObject])
  }

  def handleUnreachable(member: Member) {
    log info s"Member detected as unreachable: $member"
    out ! (Json.obj("state" -> "unreachable") ++ Json.toJson(member).as[JsObject])

  }

  def handleRemoved(member: Member, previousStatus: MemberStatus) {
    log info s"Member is Removed: ${member.address} after $previousStatus"
    out ! (Json.obj("state" -> "removed") ++ Json.toJson(member).as[JsObject])
  }

  def handleExit(member: Member) {
    log info s"Member is exiting cluster: $member"
    out ! (Json.obj("state" -> "exit") ++ Json.toJson(member).as[JsObject])
  }

}

object MonitorActor {
  def props(out: ActorRef) = Props(new MonitorActor(out))
}