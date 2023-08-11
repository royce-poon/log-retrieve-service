package scala

import java.io.File

import log.LogService
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global

class LogServiceSpec extends AnyFunSpec with MockitoSugar {

  describe("getLogLines") {

    val logService = new LogService()

    it("should return results successfully without any filters") {
      val filePath = getClass.getClassLoader.getResource("tinyfile.log").getFile
      val file = new File(filePath)
      val lines = logService.getLogLines(file)

      lines shouldBe Seq(
        "2023-08-09 10:07:00 - INFO - Request received",
        "2023-08-09 10:06:00 - WARNING - Server overload",
        "2023-08-09 10:05:00 - INFO - Data processing completed",
        "2023-08-09 10:04:00 - ERROR - Application crashed",
        "2023-08-09 10:03:00 - WARNING - Resource limit exceeded",
        "2023-08-09 10:02:00 - INFO - User logged in",
        "2023-08-09 10:01:00 - ERROR - Database connection failed",
        "2023-08-09 10:00:00 - INFO - Application started"
      )
    }

    it("should return expected lines using keyword param") {
      val filePath = getClass.getClassLoader.getResource("tinyfile.log").getFile
      val file = new File(filePath)
      val keyword = Some("error")
      val lines = logService.getLogLines(file, keywordOpt = keyword)

      lines shouldBe Seq(
        "2023-08-09 10:04:00 - ERROR - Application crashed",
        "2023-08-09 10:01:00 - ERROR - Database connection failed"
      )
    }

    it("should return expected lines using lastNumEntries param") {
      val filePath = getClass.getClassLoader.getResource("tinyfile.log").getFile
      val file = new File(filePath)
      val lastNumEntries = Some(2)
      val lines = logService.getLogLines(file, lastNumEntriesOpt = lastNumEntries)

      lines shouldBe Seq(
        "2023-08-09 10:07:00 - INFO - Request received",
        "2023-08-09 10:06:00 - WARNING - Server overload"
      )
    }

    it("should return expected paginated lines without using page param") {
      val filePath = getClass.getClassLoader.getResource("bigfile.log").getFile
      val file = new File(filePath)
      val lines = logService.getLogLines(file)

      lines.size shouldBe 100
      lines.head shouldBe "2023-08-09 12:59:00 - INFO - Account created"
      lines.last shouldBe "2023-08-09 11:20:00 - WARNING - Disk space cleanup needed"
    }

    it("should return expected paginated lines using page param") {
      val filePath = getClass.getClassLoader.getResource("bigfile.log").getFile
      val file = new File(filePath)
      val page = Some(2)
      val lines = logService.getLogLines(file, pageOpt = page)

      lines.size shouldBe 100
      lines.head shouldBe "2023-08-09 11:19:00 - INFO - System upgrade initiated"
      lines.last shouldBe "2023-08-09 12:40:00 - WARNING - Disk space low"
    }

    it("should return expected paginated lines using pagination + keyword params") {
      val filePath = getClass.getClassLoader.getResource("bigfile.log").getFile
      val file = new File(filePath)
      val keyword = Some("info")
      val page = Some(2)
      val lines = logService.getLogLines(file, keywordOpt = keyword, pageOpt = page)

      lines.size shouldBe 80
      lines.head shouldBe "2023-08-09 12:39:00 - INFO - User preferences updated"
      lines.last shouldBe "2023-08-09 10:00:00 - INFO - Application started"
    }
  }
}