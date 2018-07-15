package jp.tooppoo.isbn.api

import dispatch.{Http, as, url}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BookApiClient {
  protected val client = Http.default

  def fetchByIsbn(isbn: String): Future[String]
  def close: Unit
}

class GoogleBookClient extends BookApiClient {
  val baseUrl = "https://www.googleapis.com/books/v1/volumes"

  def fetchByIsbn(isbn: String): Future[String] = {
    val query = s"q=isbn:$isbn"
    val svc = url(s"$baseUrl?$query")

    client(svc OK as.String)
  }

  def close = Http.default.client.close()
}

object BookApiClient {
  val fromGoogleBooks = new GoogleBookClient
}
