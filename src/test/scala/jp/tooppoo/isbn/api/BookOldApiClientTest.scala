package jp.tooppoo.isbn.api

import org.scalatest.{AsyncWordSpec, Matchers}
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class BookOldApiClientTest extends AsyncWordSpec with Matchers {
  private def withGoogleClient(testcase: BookApiClient => Future[Assertion]): Future[Assertion] = {
    testcase(BookApiClient.fromGoogleBooks)
  }

  "ISBN 4048869515" in withGoogleClient { client =>
    for { json <- client.fetchByIsbn("4048869515")} yield {
      json should include ("ECサイト「4モデル式」戦略マーケティング")
    }
  }
}
