package jp.tooppoo.isbn.model

case class Book(
                 name: String,
                 authors: Seq[String],
                 publisher: String,
                 publishedAt: String,
                 printType: String,
                 pageCount: Option[Int],
                 price: Option[Long],
                 isbn10: String,
                 isbn13: String
               ) {
}

object Book {
  def apply(
             name: String,
             authors: Seq[String],
             publisher: String,
             publishedAt: String,
             printType: String,
             pageCount: Option[Int],
             price: Option[Long],
             isbn10: String,
             isbn13: String
           ) = new Book(name, authors, publisher, publishedAt, printType, pageCount, price, isbn10, isbn13)
}
