package jp.tooppoo.isbn.service

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.model.Book
import jp.tooppoo.isbn.model.Book.FetchedBookRecord
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookLoader(private val client: BookApiClient) {
  val logger = LoggerFactory.getLogger(BookLoader.getClass)

  def load(isbnList: Seq[String]): Future[Seq[FetchedBookRecord]] = {
    val fetchFutures: Seq[Future[String]] = isbnList.map { isbn =>
      client.fetchByIsbn(isbn) recover {
        case r => {
          logger.debug(s"failed to fetch book by isbn = $isbn")
          r.toString // APIコールが失敗しても、レスポンスはそのまま使用する
        }
      }
    }

    Future.sequence(fetchFutures).map { jsonList =>
      logger.debug("Future.sequence complete")
      logger.debug(s"jsonList = $jsonList")

      for (json <- jsonList) yield {
        val book = Book.parseJson(json)

        logger.debug(s"book = $book")

        book
      }
    }
  }

  def close = client.close
}

object BookLoader {

  def apply(client: BookApiClient) = new BookLoader(client)
}