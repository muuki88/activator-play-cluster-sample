// TODO Set your organization here
organization := "your.organization"

// TODO Set your version here
version := "1.0"

// Scala Version, Play supports both 2.10 and 2.11
//scalaVersion := "2.10.4"
scalaVersion := "2.11.1"

lazy val frontend = (project in file("frontend"))
    .enablePlugins(PlayScala)
    .settings(
        name := "frontend",
        libraryDependencies ++= (Dependencies.frontend  ++ Seq(filters, cache)),
        pipelineStages := Seq(rjs, digest, gzip),
        RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))
    )

lazy val backend = (project in file("backend"))
    .settings(
        name := "backend",
        libraryDependencies ++= Dependencies.backend
    )

//
// Scala Compiler Options
// If this project is only a subproject, add these to a common project setting.
//
scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code"
)

