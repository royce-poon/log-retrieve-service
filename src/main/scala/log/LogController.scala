package log

import java.io.File

import javax.inject._
import play.api.mvc._

import scala.util.{Failure, Success, Try}

@Singleton
class LogController @Inject()(cc: ControllerComponents, logService: LogService) extends AbstractController(cc) {

  // Directory that contains the log files
  private val logRootDir = "/var/log/"

  def getLogs(fileName: String, keyword: Option[String], lastNumEntries: Option[Int], page: Option[Int]) = Action {

    val file = Try(new File(s"$logRootDir$fileName"))

    file match {
      case Success(file) =>
        val paginatedLines = Try {
          logService.getLogLines(file, keyword, lastNumEntries, page)
        } match {
          case Success(lines) => lines
          case Failure(exception) =>
            val errorMessage = s"An error occurred: ${exception.getMessage}"
            List(errorMessage)
        }

        Ok(paginatedLines.mkString("\n"))
      case Failure(_) =>
        BadRequest("The file cannot be found or opened")
    }
  }
}