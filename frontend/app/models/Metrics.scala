package models

import akka.actor._
import akka.cluster.{ Cluster, NodeMetrics }
import akka.cluster.ClusterEvent._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

/**
 * <h1>Websocket API</h1>
 * 
 * Defines the json API for cluster metrics.
 * It duplicates 
 */
object Metrics {

  /**
   * A metric should always have
   * - a name
   * - an address to which node it belongs
   * - a timestamp (when it was recorded)
   */
  sealed trait NodeMetric {
    val name: String
    val address: Address
    val timestamp: Long
  }

  case class Cpu(address: Address, timestamp: Long,
    systemLoadAverage: Option[Double],
    cpuCombined: Option[Double],
    processors: Int,
    name: String = "cpu") extends NodeMetric

  case class HeapMemory(address: Address, timestamp: Long,
    used: Long,
    committed: Long,
    max: Option[Long],
    name: String = "heap") extends NodeMetric

  /**
   * Transforms a json to an akka.actor.Address
   * 
   * The companion object overrides the apply methods,
   * so the macro creation fails and we have to implement
   * it by hand.
   */
  val addressReads: Reads[Address] = Reads { js =>
    val proto = (js \ "protocol").as[String]
    val system = (js \ "system").as[String]
    val host = (js \ "host").asOpt[String]
    val port = (js \ "port").asOpt[Int]

    val address = (host, port) match {
      case (Some(host), Some(port)) => Address(proto, system, host, port)
      case (_, _)                   => Address(proto, system)
    }

    JsSuccess(address)
  }

  /**
   * Transforms an akka.actor.Address to json.
   */
  val addressWrites: Writes[Address] = Writes { a =>
    Json.obj(
      "protocol" -> a.protocol,
      "system" -> a.system,
      "host" -> a.host,
      "port" -> a.port,
      "full" -> a.toString
    )
  }
  
  // Websocket formatter
  implicit val addressFormat: Format[Address] = Format(addressReads, addressWrites)
  implicit val addressFormatter = FrameFormatter.jsonFrame[Address]

  implicit val heapMemoryFormat: Format[HeapMemory] = Json.format[HeapMemory]
  implicit val heapMemoryFormatter = FrameFormatter.jsonFrame[HeapMemory]

  implicit val cpuFormat: Format[Cpu] = Json.format[Cpu]
  implicit val cpuFormatter = FrameFormatter.jsonFrame[Cpu]

  // Actual node metrics formats
  val nodeMetricReads: Reads[NodeMetric] = Reads {
    case js if (js \ "name") == "cpu"  => heapMemoryFormat reads js
    case js if (js \ "name") == "heap" => cpuFormat reads js
  }

  val nodeMetricWrites: Writes[NodeMetric] = Writes {
    case m: HeapMemory => heapMemoryFormat writes m
    case m: Cpu        => cpuFormat writes m
    case _             => throw new RuntimeException
  }

  implicit val nodeMetricFormat: Format[NodeMetric] = Format(nodeMetricReads, nodeMetricWrites)
  implicit val nodeMetricFormatter = FrameFormatter.jsonFrame[NodeMetric]

}