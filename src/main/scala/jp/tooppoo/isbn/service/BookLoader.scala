package jp.tooppoo.isbn.service

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.model.Book
import jp.tooppoo.isbn.model.Book.FetchedBookRecord

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookLoader(private val client: BookApiClient) {
  def load(isbnList: Seq[String]): Future[Seq[FetchedBookRecord]] = {
    val fetchFutures: Seq[Future[String]] = isbnList.map (client.fetchByIsbn)

    Future.sequence(fetchFutures).map {
      for (json <- _) yield Book.parseJson(json)
    }
  }
}

object BookLoader {
  def apply(client: BookApiClient) = new BookLoader(client)
}