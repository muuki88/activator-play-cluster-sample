package backend

import akka.actor._
import com.typesafe.config.ConfigFactory
import backend.factorial._

/**
 * Booting a cluster backend node with all actors
 */
object Backend extends App {

  // Simple cli parsing
  val port = args match {
    case Array()     => "0"
    case Array(port) => port
    case args        => throw new IllegalArgumentException(s"only ports. Args [ $args ] are invalid")
  }

  // System initialization
  val properties = new java.util.Properties
  properties setProperty ("akka.remote.netty.tcp.port", port)
  val system = ActorSystem("application", (ConfigFactory parseProperties properties)
    .withFallback(ConfigFactory.load())
  )
  
  // Deploy actors and services
  FactorialBackend startOn system
}
