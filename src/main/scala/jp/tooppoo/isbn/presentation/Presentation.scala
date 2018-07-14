package jp.tooppoo.isbn.presentation

import jp.tooppoo.isbn.model.Book.FetchedBookRecord
import jp.tooppoo.isbn.model.{Book, InvalidBookRecord}

trait Presentation {
  protected def convertPrintType(label: String): String = {
    label match {
      case "BOOK" => "本"
      case "MAGAZINE" => "雑誌"
      case _ => "電子書籍" // TODO GoogleAPiがBOOK/MAGAZINEのみなので、とりあえず電子書籍扱い.
    }

  }
  def transform(books: Seq[Either[InvalidBookRecord, Seq[Book]]]): String
}

class CsvPresentation extends Presentation {
  def transform(books: Seq[FetchedBookRecord]): String = {
    val rows = books.flatMap {
      case Right(books) => {
        books.map { book =>
          val row: Seq[String] = Seq(
            book.isbn10,
            book.isbn13,
            "",
            "",
            "読み終わった", // TODO とりあえず固定
            "",
            "",
            "",
            "", // TODO ブクログでの読み取り日時? とりあえず空欄
            "", // TODO ブクログでの読み取り日時? とりあえず空欄
            book.name,
            book.authors.mkString(" "),
            book.publisher,
            book.publishedAt,
            convertPrintType(book.printType),
            book.pageCount.getOrElse("").toString,
            book.price.getOrElse("").toString
          )
          row.mkString(",")
        }
      }
      case Left(invalid) => {
        val prefix = "ERROR"
        val message = invalid.cause.message
        val json = invalid.rawJson

        Seq(s"$prefix: $message $json")
      }
    }
    rows.mkString("\n")
  }
}

object Presentation {
  def asCSV: Presentation = new CsvPresentation
}
