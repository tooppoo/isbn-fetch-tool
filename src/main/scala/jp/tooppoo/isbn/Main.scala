package jp.tooppoo.isbn

import jp.tooppoo.isbn.service.BookLoadService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Main extends App {
  val logger = LoggerFactory.getLogger(this.getClass)

  val file = Source.fromFile(args(0), "UTF-8")
  val isbnList = file.getLines.toArray

  file.close

  for { output <- BookLoadService.withGoogle.load(isbnList) } {
    logger.debug(s"output = $output")
    println(output)
  }
}
