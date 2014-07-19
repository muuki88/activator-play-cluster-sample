package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.cluster._

object Member {

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

}