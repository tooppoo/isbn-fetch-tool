package jp.tooppoo.isbn

import jp.tooppoo.isbn.cli.IsbnOptionParser
import jp.tooppoo.isbn.service.BookLoadService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Main extends App {
  val logger = LoggerFactory.getLogger(this.getClass)

  val options = IsbnOptionParser.parse(args).build

  options match {
    case Right(config) =>
      val apiKey = config.apiKey

      val file = Source.fromFile(config.source, "UTF-8")
      val isbnList = file.getLines.toArray

      file.close

      for { output <- BookLoadService.withGoogle.load(isbnList, apiKey) } {
        logger.debug(s"output = $output")
        println(output)
      }
    case Left(messages) => for (message <- messages) println(message)
  }
}
