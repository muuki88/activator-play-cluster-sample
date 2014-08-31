import sbt._

object Dependencies {

  object Version {
    val akka = "2.3.4"
    val kamon = "0.3.4"
  }

  lazy val frontend = common ++ webjars ++ play_metrics
  lazy val backend = common ++ metrics

  val common = Seq(
    "com.typesafe.akka" %% "akka-actor" % Version.akka,
    "com.typesafe.akka" %% "akka-cluster" % Version.akka,
    "com.google.guava" % "guava" % "17.0"
  )

  val webjars = Seq(
    "org.webjars" % "requirejs" % "2.1.11-1",
    "org.webjars" % "underscorejs" % "1.6.0-3",
    "org.webjars" % "jquery" % "1.11.1",
    "org.webjars" % "d3js" % "3.4.9",
    "org.webjars" % "bootstrap" % "3.2.0" exclude ("org.webjars", "jquery"),
    "org.webjars" % "bootswatch-yeti" % "3.2.0" exclude ("org.webjars", "jquery"),
    "org.webjars" % "angularjs" % "1.2.16-2" exclude ("org.webjars", "jquery")
  )
  
  val kamon = Seq(
    "io.kamon"    %% "kamon-core"           % Version.kamon,
    "io.kamon"    %% "kamon-statsd"         % Version.kamon,
    "io.kamon"    %% "kamon-log-reporter"   % Version.kamon,
    "io.kamon"    %% "kamon-system-metrics" % Version.kamon,
    "org.aspectj" % "aspectjweaver"         % "1.8.1"    
  )
  
  val play_metrics = kamon ++ Seq(
    "io.kamon" %% "kamon-play" % Version.kamon
  )

  val metrics = kamon ++ Seq(
    "org.fusesource" % "sigar" % "1.6.4"
  )

}
