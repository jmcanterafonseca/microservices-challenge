import db.{Database}
import javax.servlet.ServletContext
import org.scalatra._
import rest.ClockService

/**
  *
  * Scalatra framework bootstrap class
  *
  *
  */
class ScalatraBootstrap extends LifeCycle with main.Configuration {
  override def init(context: ServletContext) {
    context.mount(new ClockService, "/*")

    context.initParameters(Endpoint) = System.getenv.getOrDefault(Endpoint, DefaultEndpoint)
    context.initParameters("org.scalatra.Port") = System.getenv.getOrDefault(Port, DefaultPort)

    // DB Params and Kafka Params
    applyConf()
  }

  override def destroy(context: ServletContext): Unit = {
    super.destroy(context)

    Database.terminate

    Console.println("Database has been terminated")
  }
}
