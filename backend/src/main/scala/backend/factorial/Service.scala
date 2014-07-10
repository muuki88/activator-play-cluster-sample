package backend.factorial

import scala.concurrent.duration._
import scala.annotation.tailrec
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.pattern.pipe
import akka.cluster.Cluster
import akka.routing.FromConfig
import api.FactorialService._

/**
 * Doing the calculation
 */
class WorkerActor extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {
    case Compute(n: Int) => Future(factorial(n)) map { Result(_) } pipeTo sender()
  }

  def factorial(n: Int): BigInt = {
    @tailrec def factorialAcc(acc: BigInt, n: Int): BigInt = {
      if (n <= 1) acc
      else factorialAcc(acc * n, n - 1)
    }
    factorialAcc(BigInt(1), n)
  }

}

/**
 * Bootup the factorial service and the associated worker actors
 */
object FactorialBackend {

  def startOn(system: ActorSystem) {
    system.actorOf(Props[WorkerActor], name = "factorialBackend")
  }

}