package jp.tooppoo.isbn.service

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.presentation.Presentation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookLoadService private (
  private val loader: BookLoader,
  private val presentor: Presentation
) {
  def load(isbnList: Seq[String]): Future[String] = for {
    books <- loader.load(isbnList)
  } yield presentor.transform(books)
}

object BookLoadService {
  val withGoogle = new BookLoadService(
    BookLoader(BookApiClient.fromGoogleBooks),
    Presentation.asCSV
  )
}
