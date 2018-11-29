name := """health rest api"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += "org.apache.kafka" %% "kafka" % "2.1.0"
libraryDependencies += "org.apache.kafka" % "kafka-tools" % "2.1.0" % Test
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.1.0" % Test
libraryDependencies += "com.salesforce.kafka.test" % "kafka-junit-core" % "3.0.1" % Test

javaOptions in Test += "-Dconfig.file=conf/test.conf"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
