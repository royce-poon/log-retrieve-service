package log

import play.api.ApplicationLoader.Context
import play.api._
import play.filters.HttpFiltersComponents
import router.Routes

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new AppComponents(context).application
  }
}

class AppComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
        with HttpFiltersComponents {

  lazy val logController = new LogController(controllerComponents, new LogService())

  override def router: Routes = new Routes(httpErrorHandler, logController)
}
