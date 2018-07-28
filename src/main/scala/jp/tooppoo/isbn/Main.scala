package jp.tooppoo.isbn

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.cli.IsbnOptionParser
import jp.tooppoo.isbn.presentation.Presentation
import jp.tooppoo.isbn.loader.BookLoader
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

      val client = BookApiClient.fromGoogleBooks

      BookLoader(client).load(isbnList, apiKey) map { books =>
        client.close
        val output = Presentation.asCSV.transform(books)

        logger.debug(s"output = $output")
        println(output)
      }
    case Left(messages) => for (message <- messages) println(message)
  }
}
