package log

import scala.concurrent.ExecutionContext

class LogService()(implicit ec: ExecutionContext) {

  def readFile(fileName: String, keywordOpt: Option[String], lastNumEntriesOpt: Option[Int]): Seq[String] = {
    
  }
}