package jp.tooppoo.isbn.api

import dispatch.{Http, Req, as, url}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BookApiClient {
  protected val client = Http.default

  def fetchByIsbn(isbn: String, apiKey: Option[String] = None): Future[String]
  def close: Unit
}

class GoogleBookClient extends BookApiClient {
  val baseUrl = "https://www.googleapis.com/books/v1/volumes"

  def fetchByIsbn(isbn: String, apiKey: Option[String] = None): Future[String] = {
    val requestBuilder = setIsbnQueryParam(isbn) andThen setApiKeyParam(apiKey)

    val request = requestBuilder(url(baseUrl))

    client(request OK as.String)
  }

  def close = Http.default.client.close()

  private val setIsbnQueryParam = ((isbn: String, request: Req) => {
    request.addQueryParameter("q", s"isbn:$isbn")
  }).curried

  private val setApiKeyParam = ((apiKey: Option[String], request: Req) => {
    apiKey match {
      case Some(key) =>
        request.addQueryParameter("key", key)
      case None => request
    }
  }).curried
}

object BookApiClient {
  val fromGoogleBooks = new GoogleBookClient
}
