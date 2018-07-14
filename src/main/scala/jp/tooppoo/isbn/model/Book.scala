package jp.tooppoo.isbn.model

import io.circe.{Json, ParsingFailure, parser}

case class Book private(json: Json) {
  private val volume = json.hcursor.downField("volumeInfo")
  private val identifier = volume.downField("industryIdentifiers").downArray

  private def findByIsbn(size: Int)(json: Json) = {
    json.hcursor.get[String]("type").right.get == s"ISBN_$size"
  }

  val name: String = volume.getOrElse[String]("title")("").right.get

  val authors: Seq[String] = volume.getOrElse[Seq[String]]("authors")(Seq.empty).right.get

  val publisher: String = volume.getOrElse[String]("publisher")("").right.get

  val publishedAt: String = volume.getOrElse[String]("publishedDate")("").right.get

  val printType: String = volume.getOrElse[String]("printType")("").right.get

  val pageCount: Option[Int] = volume.getOrElse[Option[Int]]("pageCount")(None).right.get

  val price: Option[Long] = volume.getOrElse[Option[Long]]("price")(None).right.get

  val isbn10: String = identifier.find(findByIsbn(10)).getOrElse[String]("identifier")("").right.get
  val isbn13: String = identifier.find(findByIsbn(13)).getOrElse[String]("identifier")("").right.get
}

case class InvalidBook(cause: ParsingFailure, rawJson: String)

object Book {
  def parseJson(json: String): Either[InvalidBook, Seq[Book]] = {
    val raw: Either[ParsingFailure, Json] = parser.parse(json)

    if (raw.isRight) {
      val rawBooks = raw.right.get.hcursor.get[Seq[Json]]("items").right.get
      val books = rawBooks.map { new Book(_) }

      Right(books)
    } else {
      Left(new InvalidBook(raw.left.get, json))
    }
  }
}
