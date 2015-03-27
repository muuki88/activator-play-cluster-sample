import sbt._

object Dependencies {

  object Version {
    val akka = "2.3.9"
  }

  lazy val frontend = common ++ webjars
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

  val metrics = Seq(
    "org.fusesource" % "sigar" % "1.6.4"
  )
  
  val tests = Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scalatestplus" %% "play" % "1.2.0" % "test"
  )

}
