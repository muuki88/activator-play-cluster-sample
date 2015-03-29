// TODO Set your organization here
organization in ThisBuild := "your.organization"

// TODO Set your version here
version in ThisBuild := "2.3.9"

scalaVersion in ThisBuild := "2.11.6"

lazy val root = (project in file("."))
  .aggregate(api, frontend, backend)
  
lazy val frontend = (project in file("frontend"))
    .enablePlugins(PlayScala)
    .settings(
        name := "frontend",
        libraryDependencies ++= (Dependencies.frontend  ++ Seq(filters, cache)),
        pipelineStages := Seq(rjs, digest, gzip),
        RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:")),
        javaOptions ++= Seq(
            "-Djava.library.path=" + (baseDirectory.value.getParentFile() / "backend" / "sigar" ).getAbsolutePath, 
            "-Xms128m", "-Xmx1024m"),
        fork in run := true
    ).dependsOn(api)

lazy val backend = (project in file("backend"))
    .settings(
        name := "backend",
        libraryDependencies ++= Dependencies.backend,
        javaOptions ++= Seq(
            "-Djava.library.path=" + (baseDirectory.value / "sigar").getAbsolutePath, 
            "-Xms128m", "-Xmx1024m"),
        // this enables custom javaOptions
        fork in run := true
    ).dependsOn(api)
    
lazy val api = (project in file("api"))
    .settings(
        name := "api",
        libraryDependencies ++= Dependencies.backend
    )

//
// Scala Compiler Options
// If this project is only a subproject, add these to a common project setting.
//
scalacOptions in ThisBuild ++= Seq(
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

