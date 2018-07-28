package jp.tooppoo.isbn.parser

import jp.tooppoo.isbn.model.Book
import org.scalatest.{Matchers, WordSpec}

import scala.io.Source
import scala.util.{Failure, Success}

class BookJsonParserTest extends WordSpec with Matchers {
  def withGoogleParser(testcase: BookJsonParser => Any): Any = {
    testcase(BookJsonParser.forGoogle)
  }

  "json parser" can {
    "parse json" when {
      "invalid format" in withGoogleParser { parser =>
        val json = """{"a":}"""

        val result = parser.parse(json)

        assert(result.isFailure)
      }
      "empty record" in withGoogleParser { parser =>
        val json = Source.fromResource("empty.json").mkString("")

        parser.parse(json) match {
          case Success(books) => assert(books.isEmpty)
          case _ => fail()
        }
      }
      "some records" in withGoogleParser { parser =>
        val json = Source.fromResource("book.json").mkString("")

        parser.parse(json) match {
          case Success(books) =>
            val body: Book = books.head

            assert(body.name == "ECサイト「4モデル式」戦略マーケティング")
            assert(body.authors == Seq("権成俊", "村上佐央里"))
            assert(body.isbn10 == "4048869515")
            assert(body.isbn13 == "9784048869515")
            assert(body.publisher == "アスキー・メディアワークス")
            assert(body.publishedAt == "2012-09")
            assert(body.printType == "BOOK")
            assert(body.pageCount.get == 239)
          case _ => fail()
        }
      }
    }
  }
}
