package log

import java.io.{File, RandomAccessFile}

import scala.concurrent.ExecutionContext

class LogService()(implicit ec: ExecutionContext) {

  // Number of log lines per page
  private val pageSize = 100

  // Buffer size for file read
  private val bufferSize = 4096

  /**
    * Get paginated log lines from a file
    *
    * @param file The log file to operate on
    * @param keywordOpt If defined, the keyword to filter results
    * @param lastNumEntriesOpt If defined, the last n number of matching results to take
    * @param pageOpt If defined, the page number of results to take
    *
    * @return A paginated list of log lines from the file
    */
  def getPaginatedLogLines(file: File, keywordOpt: Option[String] = None, lastNumEntriesOpt: Option[Int] = None, pageOpt: Option[Int] = None): Seq[String] = {

    // Used to indicate whether or not there are still some lines to be read if currentPos is negative.
    var hasRemainingLines = true
    var counter = 0
    val takeN = lastNumEntriesOpt.getOrElse(-1)

    val randomAccessFile = new RandomAccessFile(file, "r")

    try {
      val lineBuffer = scala.collection.mutable.Buffer.empty[String]

      // Starting position to read bytes
      var currentPos = file.length() - bufferSize
      var bytesRead = 0

      while (currentPos >= 0 || hasRemainingLines) {
        if (hasRemainingLines && currentPos < 0) {
          //Set this to false so that the while loop doesn't execute again should currentPos becomes negative for the second time
          hasRemainingLines = false
        }

        val (adjustedCurrentPos, adjustedPosCount) = adjustFilePointerPos(randomAccessFile, currentPos)
        currentPos = adjustedCurrentPos

        randomAccessFile.seek(currentPos)
        val buffer = new Array[Byte](bufferSize - adjustedPosCount)
        bytesRead = randomAccessFile.read(buffer)

        if (bytesRead > 0) {
          val lines = new String(buffer, 0, bytesRead).split("\n")

          for (line <- lines.reverse) {
            if (!line.isEmpty) {
              // Checking whether log line contains keyword (case insensitive)
              val containsKeyword = keywordOpt.map(keyword => line.toLowerCase.indexOf(keyword.toLowerCase)).getOrElse(0) != -1
              if (containsKeyword && (takeN == -1 || counter < takeN)) {
                lineBuffer.append(line.trim)
                counter = counter + 1
              }
            }
          }
        }

        currentPos -= bufferSize
      }

      getPaginatedLines(lineBuffer, pageOpt)
    } finally {
      randomAccessFile.close()
    }
  }

  /**
    * Adjust the current file pointer position to the position of the next new line character "\n"
    *
    * @return A tuple containing 1)a position that points to the next new line character and 2)the number of positions that was adjusted
    */
  private def adjustFilePointerPos(randomAccessFile: RandomAccessFile, currentPos: Long): (Long, Int) = {
    var newCurrentPos = currentPos
    var adjustedPosCount = 0

    if (currentPos < 0) {
      (0, -currentPos.toInt)
    } else {
      do {
        newCurrentPos = newCurrentPos + 1
        adjustedPosCount = adjustedPosCount + 1
        randomAccessFile.seek(newCurrentPos)
      } while (randomAccessFile.readByte() != 0xA)

      (newCurrentPos, adjustedPosCount)
    }
  }

  private def getPaginatedLines(lines: Seq[String], page: Option[Int]) = {
    val pageToTake = page.getOrElse(1)
    val startIndex = (pageToTake - 1) * pageSize
    val endIndex = startIndex + pageSize
    lines.slice(startIndex, endIndex)
  }
}