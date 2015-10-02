package actors

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.metrics.StandardMetrics.{HeapMemory, Cpu}
import akka.cluster.metrics.{ClusterMetricsExtension, ClusterMetricsChanged, NodeMetrics}
import play.api.libs.json._

import models.Metrics

/**
 * Created by the playframework for a websocket connection.
 * Listens to ClusterMetricsChanged events and pushs them to the websocket.
 * 
 * @param out - the websocket to which we can push messages
 */
class MetricsActor(out: ActorRef) extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  val extension = ClusterMetricsExtension(context.system)

  // subscribe to ClusterMetricsChanged
  override def preStart() = extension.subscribe(self)

  // clean up
  override def postStop() = extension.unsubscribe(self)

  // handle the events
  def receive = {
    case ClusterMetricsChanged(metrics) => metrics foreach handleMetrics
    case state: CurrentClusterState     => // ignore
  }

  def handleMetrics(metrics: NodeMetrics) = {
    pushHeap(metrics)
    pushCpu(metrics)
  }

  def pushHeap(nodeMetrics: NodeMetrics): Unit = nodeMetrics match {
    case HeapMemory(address, timestamp, used, committed, max) =>
      out ! Metrics.HeapMemory(address, timestamp, used, committed, max)
    case _ => // no heap info
  }

  def pushCpu(nodeMetrics: NodeMetrics): Unit = nodeMetrics match {
    case Cpu(address, timestamp, systemLoadAverage, cpuCombined, cpuStolen, processors) =>
      out ! Metrics.Cpu(address, timestamp, systemLoadAverage, cpuCombined, processors)
    case _ => // no cpu info
  }

}

object MetricsActor {

  /**
   * actor definition
   */
  def props(out: ActorRef) = Props(new MetricsActor(out))

}