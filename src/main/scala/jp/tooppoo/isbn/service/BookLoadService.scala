package jp.tooppoo.isbn.service

import jp.tooppoo.isbn.api.BookApiClient
import jp.tooppoo.isbn.presentation.Presentation
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookLoadService private (
  private val loader: BookLoader,
  private val presentor: Presentation
) {
  val logger = LoggerFactory.getLogger(this.getClass)

  def load(isbnList: Seq[String]): Future[String] = loader.load(isbnList).map { books =>
    loader.close

    presentor.transform(books)
  }
}

object BookLoadService {
  val withGoogle = new BookLoadService(
    BookLoader(BookApiClient.fromGoogleBooks),
    Presentation.asCSV
  )
}
