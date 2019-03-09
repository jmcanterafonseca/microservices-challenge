package main

import kafka.{ClockRecordConsumer, KafkaParams}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
  *
  * Server launcher
  *
  *
  *
  */
object JettyLauncher extends Configuration  {
  def main(args:Array[String]) = {
    val port = System.getenv().getOrDefault(Port,DefaultPort).toInt

    val server = new Server(port)
    val context = new WebAppContext()

    context setContextPath "/"
    context.setResourceBase("src/main/webapp")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)

    applyConf

    val kafkaBroker = System.getenv.getOrDefault(KafkaBroker, DefaultKafkaBroker)
    ClockRecordConsumer.setParams(KafkaParams(kafkaBroker))

    val kafkaThread = new Thread {
      override def run = {
        ClockRecordConsumer.consume()
      }
    }

    kafkaThread.start

    server.start
    server.join
  }
}
