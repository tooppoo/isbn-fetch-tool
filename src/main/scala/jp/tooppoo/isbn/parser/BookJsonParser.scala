package jp.tooppoo.isbn.parser

import io.circe.{Json, ParsingFailure, parser}
import jp.tooppoo.isbn.model.Book
import jp.tooppoo.isbn.parser.BookJsonParser.{InvalidBookRecord, ParsedBooks}
import org.slf4j.LoggerFactory

trait BookJsonParser {
  def parse(json: String): ParsedBooks
}

class GoogleBooksParserBook extends BookJsonParser {
  val logger = LoggerFactory.getLogger("Book::parseJson")

  def parse(json: String): ParsedBooks = {
    val raw: Either[ParsingFailure, Json] = parser.parse(json)

    raw match {
      case Right(validRaw) => {
        logger.debug("success to parse json")

        val totalItems = validRaw.hcursor.get[Int]("totalItems").getOrElse(0)

        if (totalItems > 0) {
          validRaw.hcursor.get[Seq[Json]]("items") match {
            case Right(rawBooks) => {
              val books = rawBooks.map(buildBook)
              Right(books)
            }
            case Left(invalid) => {
              logger.debug("fail to get items")
              logger.debug(s"json = $json")

              Left(new InvalidBookRecord(invalid, json, ""))
            }
          }
        } else {
          Right(Seq.empty)
        }
      }
      case Left(invalidRaw) => {
        logger.debug("fail to parse json")
        logger.debug(s"json = $json")

        Left(new InvalidBookRecord(invalidRaw, json, ""))
      }
    }
  }

  private val buildBook: Json => Book = { json =>
    val volume = json.hcursor.downField("volumeInfo")
    val identifier = volume.downField("industryIdentifiers").downArray

    val name: String = volume.getOrElse[String]("title")("").right.get

    val authors: Seq[String] = volume.getOrElse[Seq[String]]("authors")(Seq.empty).right.get

    val publisher: String = volume.getOrElse[String]("publisher")("").right.get

    val publishedAt: String = volume.getOrElse[String]("publishedDate")("").right.get

    val printType: String = volume.getOrElse[String]("printType")("").right.get

    val pageCount: Option[Int] = volume.getOrElse[Option[Int]]("pageCount")(None).right.get

    val price: Option[Long] = volume.getOrElse[Option[Long]]("price")(None).right.get

    val isbn10: String = identifier.find(findIsbn(10)).getOrElse[String]("identifier")("").right.get
    val isbn13: String = identifier.find(findIsbn(13)).getOrElse[String]("identifier")("").right.get

    Book(
      name,
      authors,
      publisher,
      publishedAt,
      printType,
      pageCount,
      price,
      isbn10,
      isbn13
    )
  }

  private def findIsbn(size: Int)(json: Json) = {
    json.hcursor.get[String]("type").right.get == s"ISBN_$size"
  }
}

object BookJsonParser {
  type ParsedBooks = Either[InvalidBookRecord, Seq[Book]]

  final case class InvalidBookRecord(cause: Exception, rawJson: String, failedIsbn: String)
  final case class BookNotFound(message: String) extends RuntimeException(message)

  val forGoogle = new GoogleBooksParserBook
}
