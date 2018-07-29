package jp.tooppoo.isbn

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.cli.IsbnOptionParser
import jp.tooppoo.isbn.presentation.Presentation
import jp.tooppoo.isbn.loader.BookLoader
import jp.tooppoo.isbn.model.IsbnReader
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Main extends App {
  val logger = LoggerFactory.getLogger(this.getClass)

  val options = IsbnOptionParser.parse(args).build

  options match {
    case Right(config) =>
      val apiKey = config.apiKey

      IsbnReader.fromFile(config.source) { list =>

        val client = BookApiClient.fromGoogleBooks

        BookLoader.apply.load(list, apiKey) map { books =>
          client.close
          val output = Presentation.asCSV.transform(books)

          logger.debug(s"output = $output")
          println(output)
        }
      }
    case Left(messages) => for (message <- messages) println(message)
  }
}
