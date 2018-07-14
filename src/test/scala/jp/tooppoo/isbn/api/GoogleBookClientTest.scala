package jp.tooppoo.isbn.api

import org.scalatest.{AsyncWordSpec, Matchers}

class GoogleBookClientTest extends AsyncWordSpec with Matchers {
  "ISBN" when {
    "4048869515" should {
      """be "ECサイト「4モデル式」戦略マーケティング """" in {
        GoogleBookClient.list(4048869515L) map { json =>
          assert(json.contains("ECサイト「4モデル式」戦略マーケティング"))
        }
      }
    }
  }
}
