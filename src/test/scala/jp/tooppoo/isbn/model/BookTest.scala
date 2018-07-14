package jp.tooppoo.isbn.model

import org.scalatest.{Matchers, WordSpec}

import scala.io.Source

class BookTest extends WordSpec with Matchers {
  "Book" when {
    "invalid format json" should {
      "is left" in {
        val json = """{"a":}"""
        val book = Book.parseJson(json)

        assert(book.isLeft)
        assert(book.left.get.rawJson == json)
      }
    }
    "valid format json" should {
      val jsonLines = Source.fromResource("book.json").getLines
      val json = jsonLines.reduce { (s1, s2) => s1 + s2 }
      val books = Book.parseJson(json)

      "is right" in {
        assert(books.isRight)
      }
      "have each properties" in {
        val body: Book = books.right.get.head

        assert(body.name == "ECサイト「4モデル式」戦略マーケティング")
        assert(body.authors == Seq("権成俊", "村上佐央里"))
        assert(body.isbn10 == "4048869515")
        assert(body.isbn13 == "9784048869515")
        assert(body.publisher == "アスキー・メディアワークス")
        assert(body.publishedAt == "2012-09")
        assert(body.printType == "BOOK")
        assert(body.pageCount == 239)
      }
    }
  }
}
