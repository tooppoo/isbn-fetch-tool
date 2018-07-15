package jp.tooppoo.isbn.presentation

import jp.tooppoo.isbn.model.{Book, InvalidBookRecord}
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
        val books = Seq(Right(Book.parseJson(json).right.get))
        val output = presenter.transform(books)

        val expected = "4048869515,9784048869515,,,読み終わった,,,,,,ECサイト「4モデル式」戦略マーケティング,権成俊 村上佐央里,アスキー・メディアワークス,2012-09,本,239,"

        assert(output == expected)
      }
    }
    "some books" in withCsvPresentation { presenter =>
      withJsonResource("books.json") { json =>
        val books = Seq(Right(Book.parseJson(json).right.get))
        val output = presenter.transform(books)

        val expected = Seq(
          "4048869515,9784048869515,,,読み終わった,,,,,,ECサイト「4モデル式」戦略マーケティング,権成俊 村上佐央里,アスキー・メディアワークス,2012-09,本,239,",
          "4492557709,9784492557709,,,読み終わった,,,,,,通販ビジネスの教科書,岩永　洋平,東洋経済新報社,2016-07-08,本,264,"
        ).mkString("\n")

        assert(output == expected)
      }
    }
    "contain invalid book" in withCsvPresentation { presenter =>
      withJsonResource("book.json") { json =>
        val validBooks = Seq(Right(Book.parseJson(json).right.get))
        val invalidJson = """{"dummy":"""
        val invalidBook = Left(Book.parseJson(invalidJson).left.get)

        val books: Seq[Either[InvalidBookRecord, Seq[Book]]] = validBooks :+ invalidBook
        val output = presenter.transform(books)


        val expected = Seq(
          "4048869515,9784048869515,,,読み終わった,,,,,,ECサイト「4モデル式」戦略マーケティング,権成俊 村上佐央里,アスキー・メディアワークス,2012-09,本,239,",
          s"ERROR: ${invalidBook.left.get.cause.getMessage} ${invalidJson}"
        ).mkString("\n")

        assert(output == expected)
      }
    }
  }
}
