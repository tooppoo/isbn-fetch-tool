package jp.tooppoo.isbn.loader

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.parser.BookJsonParser
import jp.tooppoo.isbn.parser.BookJsonParser.{InvalidBookRecord, ParsedBooks}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait MixinApiClient {
  protected val client: BookApiClient
}

object MixinApiClient {
  trait MixinGoogleClient extends MixinApiClient {
    protected val client = BookApiClient.fromGoogleBooks
  }
}

trait MixinJsonParser {
  protected val parser: BookJsonParser
}
object MixinJsonParser {
  trait MixinGoogleParser extends MixinJsonParser {
    protected val parser = BookJsonParser.forGoogle
  }
}


trait BookLoader extends MixinApiClient with MixinJsonParser {
  val logger = LoggerFactory.getLogger(BookLoader.getClass)

  def load(isbnList: Seq[String], apiKey: Option[String] = None): Future[Seq[ParsedBooks]] = {
    val fetchFutures: Seq[Future[(String, String)]] = isbnList.map { isbn =>
      client.fetchByIsbn(isbn, apiKey) map { json =>
        (json, isbn)
      } recover {
        case r => {
          logger.debug(s"failed to fetch book by isbn = $isbn")
          (r.toString, isbn) // APIコールが失敗しても、レスポンスはそのまま使用する
        }
      }
    }

    Future.sequence(fetchFutures).map { pairList =>
      logger.debug("Future.sequence complete")
      logger.debug(s"jsonList = $pairList")

      for ((json, isbn) <- pairList) yield {
        val book = parser.parse(json)

        logger.debug(s"book = $book")

        book match {
          case Success(books) => Right(books)
          case Failure(invalid) => {
            Left(new InvalidBookRecord(invalid, json, isbn))
          }
        }
      }
    }
  }

  def close = client.close
}

object BookLoader {
  val google = new BookLoader with MixinApiClient.MixinGoogleClient with MixinJsonParser.MixinGoogleParser
  val default = google

  def apply = default
}

