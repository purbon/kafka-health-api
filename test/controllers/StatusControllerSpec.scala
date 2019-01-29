package controllers

import play.api.libs.json.{JsLookupResult, JsString}
import play.api.test.Helpers._
import play.api.test._
import utils.KafkaPlaySpec

class StatusControllerSpec extends KafkaPlaySpec {


  "StatusController GET" should {

    "render the health page with the current status" in {
      val controller = inject[StatusController]
      val health = controller.health().apply(FakeRequest(GET, "/"))

      status(health) mustBe OK

      val color: JsLookupResult = contentAsJson(health) \ "status" \ "semaphore"
      assert(color.get == JsString("Green"))
    }

    "render the cluster guarantees" in {

      val controller = inject[StatusController]
      val guarantees = controller.clusterGuaranties().apply(FakeRequest(GET, "/cluster/guaranties"))

      status(guarantees) mustBe OK

      val producers = contentAsJson(guarantees) \ "producer"

      assert( producers.get == JsString("JMX not reachable"))
    }
  }

}
