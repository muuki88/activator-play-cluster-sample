package cluster

import scala.language.postfixOps
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.concurrent.Eventually._
import org.scalatest.time._
import org.scalatestplus.play._
import scala.concurrent.duration._
import play.api._
import play.api.http.Status._
import play.api.libs.ws._
import play.api.test._
import akka.testkit._
import actors.services.FactorialService
import play.api.libs.concurrent.Akka
import actors.services.FactorialWebsocketActor
import akka.actor.ActorDSL._
import akka.actor._

/**
 * Factorial specification
 */
class FactorialSpec extends PlaySpec with OneServerPerSuite with BeforeAndAfterAll with BeforeAndAfterEach {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      additionalConfiguration = Map(
        "ehcacheplugin" -> "disabled",
        "akka.remote.netty.tcp.port" -> "2551",
        "akka.cluster.roles.0" -> "frontend",
        "akka.cluster.roles.1" -> "backend",
        "akka.log-dead-letters" -> "0",
        "akka.actor.deployment./factorialService/factorialWorkerRouter.cluster.allow-local-routees" -> "on",
        "log-dead-letters-during-shutdown" -> "off",
        "akka.loglevel" -> "ERROR"
      ),
      withGlobal = Some(FactorialSpecSettings)
    )

  // get the play actor system
  implicit val system = Akka.system(app)

  // used to configure 'eventually'
  implicit val patienceConfig = PatienceConfig(
    timeout = scaled(Span(15, Seconds)),
    interval = scaled(Span(150, Millis))
  )

  // start actors needed to run factorialService
  override def beforeAll() {
    val backend = system.actorOf(Props[FactorialBackendMock], "factorialBackend")
  }

  "A factorial actor service" must {

    // will store results that would normally be send to a websocket
    val messages = inbox()

    // factorial websocket actor
    val socket = system.actorOf(FactorialWebsocketActor.props(messages.getRef))

    "return a Result on Compute" in {
      eventually {
        socket ! api.FactorialService.Compute(0)
        val factorial = messages.receive(1 second)
        factorial mustBe a[api.FactorialService.Result]
      }
    }
  }
}

/**
 * No production settings. Only start the service
 * we need.
 */
object FactorialSpecSettings extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    FactorialService startOn Akka.system(app)
  }
}

/**
 * Dummy backend implementation to spin off a cluster
 */
class FactorialBackendMock extends Actor {
  def receive = {
    case api.FactorialService.Compute(n: Int) => sender ! api.FactorialService.Result(n)
  }
}
