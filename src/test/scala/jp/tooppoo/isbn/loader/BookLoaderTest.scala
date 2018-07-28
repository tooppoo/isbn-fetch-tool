package jp.tooppoo.isbn.loader

import jp.tooppoo.isbn.api.BookApiClient
import org.scalatest.{Assertion, AsyncWordSpec, Matchers}

import scala.concurrent.Future

class BookLoaderTest extends AsyncWordSpec with Matchers {
  def withGoogleClient(testcase: BookApiClient => Future[Assertion]): Future[Assertion] = {
    testcase(BookApiClient.fromGoogleBooks)
  }
  "isbn list" when {
    "none" in withGoogleClient { client =>
      val list = Seq.empty
      val loader = BookLoader(client)

      loader.load(list) map {
        _.length should be (0)
      }
    }
    "single" in withGoogleClient { client =>
      val list = Seq("4048869515")
      val loader = BookLoader(client)

      loader.load(list) map {
        _.length should be (1)
      }
    }
    "multi" in withGoogleClient { client =>
      val list = Seq(
        "4048869515",
        "4492557709"
      )
      val loader = BookLoader(client)

      loader.load(list) map {
        _.length should be (2)
      }
    }
  }
}
