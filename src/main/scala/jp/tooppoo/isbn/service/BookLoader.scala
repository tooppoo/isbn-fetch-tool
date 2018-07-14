package jp.tooppoo.isbn.service

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.model.{Book, InvalidBook}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookLoader(private val client: BookApiClient) {
  def load(isbnList: Seq[String]): Future[Seq[Either[InvalidBook, Seq[Book]]]] = {
    val fetchFutures: Seq[Future[String]] = isbnList.map (client.fetchByIsbn)

    Future.sequence(fetchFutures).map {
      for (json <- _) yield Book.parseJson(json)
    }
  }
}

object BookLoader {
  def apply(client: BookApiClient) = new BookLoader(client)
}