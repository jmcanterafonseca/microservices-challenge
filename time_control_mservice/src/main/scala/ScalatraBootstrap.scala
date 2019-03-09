import javax.servlet.ServletContext
import org.scalatra._
import rest.TimeControlService

/**
  *
  *  Scalatra framework bootstrap class
  *
  *
  */
class ScalatraBootstrap extends LifeCycle with main.Configuration {
  override def init(context: ServletContext) {
    context.mount(new TimeControlService, "/*")

    context.initParameters(Endpoint) = System.getenv.getOrDefault(Endpoint,DefaultEndpoint)
    context.initParameters("org.scalatra.Port") = System.getenv.getOrDefault(Port,DefaultPort)

    applyConf()
  }
}
