
name := "Challenge-Alarm_Handler"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies +=  "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.1.0"

libraryDependencies += "org.apache.commons" % "commons-email" % "1.5"

val ScalatraVersion = "2.6.3"

javaOptions in Universal ++= Seq("-server", "-XX:+UseG1GC")

resolvers += Classpaths.typesafeReleases

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

version in Docker := "latest"
packageName in Docker := "jmcanterafonseca/challenge_alarm_handler_service"

dockerBaseImage := "openjdk:jre-alpine"

mainClass in Compile := Some("main.Launcher")
