package jp.tooppoo.isbn.loader

import org.scalatest.{Assertion, AsyncWordSpec, Matchers}

import scala.concurrent.Future

class BookLoaderTest extends AsyncWordSpec with Matchers {
  def withGoogleClient(testcase: BookLoader => Future[Assertion]): Future[Assertion] = {
    testcase(BookLoader.google)
  }
  "isbn list" when {
    "none" in withGoogleClient { loader =>
      val list = Seq.empty

      loader.load(list) map {
        _.length should be (0)
      }
    }
    "single" in withGoogleClient { loader =>
      val list = Seq("4048869515")

      loader.load(list) map {
        _.length should be (1)
      }
    }
    "multi" in withGoogleClient { loader =>
      val list = Seq(
        "4048869515",
        "4492557709"
      )

      loader.load(list) map {
        _.length should be (2)
      }
    }
  }
}
