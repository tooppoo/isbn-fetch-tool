package jp.tooppoo.isbn.presentation

import jp.tooppoo.isbn.parser.BookJsonParser.ParsedBooks

trait Presentation {
  protected def convertPrintType(label: String): String = {
    label match {
      case "BOOK" => "本"
      case "MAGAZINE" => "雑誌"
      case _ => "電子書籍" // TODO GoogleAPiがBOOK/MAGAZINEのみなので、とりあえず電子書籍扱い.
    }

  }
  def transform(books: Seq[ParsedBooks]): String
}

class CsvPresentation extends Presentation {
  class CsvSanitizer(str: String) {
    def sanitize: String = str.replace(",", ".")
    def asInline: String = str.replace("\n", " ")
  }
  implicit def canSanitizeForCSV(str: String): CsvSanitizer = new CsvSanitizer(str)

  def transform(books: Seq[ParsedBooks]): String = {
    val rows = books.flatMap {
      case Right(validBooks) =>
        validBooks.map { book =>
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
            book.name.sanitize,
            book.authors.map(_.sanitize).mkString(" "),
            book.publisher.sanitize,
            book.publishedAt,
            convertPrintType(book.printType),
            book.pageCount.getOrElse("").toString,
            book.price.getOrElse("").toString.sanitize
          )
          row.mkString(",")
        }
      case Left(invalid) =>
        val prefix = "ERROR"
        val failedIsbn = invalid.failedIsbn
        val message = invalid.cause.getMessage.asInline
        val json = invalid.rawJson.asInline

        Seq(s""""$prefix: $failedIsbn $message $json"""")
    }
    rows.mkString("\n")
  }
}

object Presentation {
  def asCSV: Presentation = new CsvPresentation
}
