package jp.tooppoo.isbn.api

import org.scalatest.{AsyncWordSpec, Matchers}
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class BookApiClientTest extends AsyncWordSpec with Matchers {
  private def withGoogleClient(testcase: BookApiClient => Future[Assertion]): Future[Assertion] = {
    testcase(BookApiClient.fromGoogleBooks)
  }

  "ISBN 4048869515" in withGoogleClient { client =>
    for { json <- client.list(4048869515L)} yield {
      json should include ("ECサイト「4モデル式」戦略マーケティング")
    }
  }
}
