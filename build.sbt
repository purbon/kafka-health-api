name := """health rest api"""
organization := "com.example"

version := "1.0-SNAPSHOT"

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

lazy val root = (project in file(".")).enablePlugins(PlayScala)

coverageEnabled := true

scalaVersion := "2.12.6"
libraryDependencies += "javax.ws.rs" % "javax.ws.rs-api" % "2.1" artifacts( Artifact("javax.ws.rs-api", "jar", "jar"))

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += {
  "org.apache.kafka" %% "kafka" % "2.1.0"
}
libraryDependencies += "org.apache.kafka" % "kafka-tools" % "2.1.0" % Test
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.1.0" % Test
libraryDependencies += "com.salesforce.kafka.test" % "kafka-junit-core" % "3.0.1" % Test

javaOptions in Test += "-Dconfig.file=conf/test.conf"
javaOptions in Test += "-Dcom.sun.management.jmxremote"
javaOptions in Test += "-Dcom.sun.management.jmxremote.port=5678"
javaOptions in Test += "-Dcom.sun.management.jmxremote.rmi.port=5678"
javaOptions in Test += "-Dcom.sun.management.jmxremote.local.only=false "
javaOptions in Test += "-Dcom.sun.management.jmxremote.ssl=false"
javaOptions in Test += "-Dcom.sun.management.jmxremote.authenticate=false"
javaOptions in Test += "-Djava.rmi.server.hostname=0.0.0.0"


test in Test := {
  Def.sequential(
    (test in Test),
    (coverageReport in Test)
  ).value
}

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
