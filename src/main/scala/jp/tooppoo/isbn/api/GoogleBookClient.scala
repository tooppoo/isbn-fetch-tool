package jp.tooppoo.isbn.api

import dispatch.{Http, as, url}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GoogleBookClient {
  val baseUrl = "https://www.googleapis.com/books/v1/volumes"
  val client = Http.default

  def booksValue(isbn: Long): Future[String] = {
    val query = s"q=isbn:$isbn"
    val svc = url(s"$baseUrl?$query")

    client(svc OK as.String)
  }
}
