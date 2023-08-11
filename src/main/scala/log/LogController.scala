package log

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class LogController @Inject()(cc: ControllerComponents, logService: LogService) extends AbstractController(cc) {

  def getLogs(fileName: String, keyword: Option[String], lastNumEntries: Option[Int], page: Option[Int]) = Action {

    Ok(Seq("test", "test").mkString("\n"))
  }
}