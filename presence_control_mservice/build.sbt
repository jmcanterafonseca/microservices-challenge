
name := "Challenge"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.1.0"

val ScalatraVersion = "2.6.3"

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.8.v20171121" % "container;compile"
)

javaOptions in Jetty ++= Seq("-server")
javaOptions in Universal ++= Seq("-server", "-XX:+UseG1GC")

resolvers += Classpaths.typesafeReleases

enablePlugins(ScalatraPlugin)
enablePlugins(JettyPlugin)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

containerPort in Jetty := 5000

version in Docker := "latest"
packageName in Docker := "jmcanterafonseca/challenge_presence_service"

dockerBaseImage := "openjdk:jre-alpine"

mainClass in Compile := Some("main.JettyLauncher")
