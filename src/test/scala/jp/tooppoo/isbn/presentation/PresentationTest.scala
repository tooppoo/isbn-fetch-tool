package jp.tooppoo.isbn.presentation

import jp.tooppoo.isbn.model.Book
import jp.tooppoo.isbn.parser.BookJsonParser.{InvalidBookRecord, ParsedBooks}
import org.scalatest.{Matchers, WordSpec}

import scala.io.Source

class PresentationTest extends WordSpec with Matchers {
  private def withCsvPresentation(testcase: Presentation => Any): Any = {
    testcase(Presentation.asCSV)
  }
  private def withJsonResource(fileName: String)(testcase: String => Any): Unit = {
    val lines = Source.fromResource(fileName).getLines
    val json = lines.reduce { (l1, l2) => l1 + l2 }

    testcase(json)
  }

  "book list" when {
    "empty" in withCsvPresentation { presenter =>
      val books = Seq.empty
      val output = presenter.transform(books)

      assert(output == "")
    }
    "single book" in withCsvPresentation { presenter =>
      withJsonResource("book.json") { json =>
        val books = Seq(
          Right(Seq(Book(
            name = "テスト",
            authors = Seq("一郎", "二郎"),
            publisher = "試験書籍",
            publishedAt = "2018-10",
            printType = "BOOK",
            pageCount = Some(100),
            price = Some(1000),
            isbn10 = "isbn10",
            isbn13 = "isbn13"
          )))
        )
        val output = presenter.transform(books)

        val expected = "isbn10,isbn13,,,読み終わった,,,,,,テスト,一郎 二郎,試験書籍,2018-10,本,100,1000"

        assert(output == expected)
      }
    }
    "some books" in withCsvPresentation { presenter =>
      withJsonResource("books.json") { json =>
        val books = Seq(
          Right(Seq(Book(
            name = "テスト1",
            authors = Seq("一郎", "二郎"),
            publisher = "試験書籍",
            publishedAt = "2018-10",
            printType = "BOOK",
            pageCount = Some(100),
            price = Some(1000),
            isbn10 = "isbn10",
            isbn13 = "isbn13"
          ))),
          Right(Seq(Book(
            name = "テスト2",
            authors = Seq("三郎"),
            publisher = "試験ブックス",
            publishedAt = "2017-01-10",
            printType = "BOOK",
            pageCount = None,
            price = None,
            isbn10 = "isbn10",
            isbn13 = "isbn13"
          )))
        )
        val output = presenter.transform(books)

        val expected = Seq(
          "isbn10,isbn13,,,読み終わった,,,,,,テスト1,一郎 二郎,試験書籍,2018-10,本,100,1000",
          "isbn10,isbn13,,,読み終わった,,,,,,テスト2,三郎,試験ブックス,2017-01-10,本,,"
        ).mkString("\n")

        assert(output == expected)
      }
    }
    "contain invalid book" in withCsvPresentation { presenter =>
      withJsonResource("book.json") { json =>
        val books: Seq[ParsedBooks] = Seq(
          Right(Seq(Book(
            name = "テスト1",
            authors = Seq("一郎", "二郎"),
            publisher = "試験書籍",
            publishedAt = "2018-10",
            printType = "BOOK",
            pageCount = Some(100),
            price = Some(1000),
            isbn10 = "isbn10",
            isbn13 = "isbn13"
          ))),
          Left(InvalidBookRecord(new RuntimeException("error test"), "{t1: 2, t2, \"test\"}", "error-isbn"))
        )
        val output = presenter.transform(books)


        val expected = Seq(
          "isbn10,isbn13,,,読み終わった,,,,,,テスト1,一郎 二郎,試験書籍,2018-10,本,100,1000",
          "\"ERROR: error-isbn error test {t1: 2, t2, \"test\"}\""
        ).mkString("\n")

        assert(output == expected)
      }
    }
  }
}
