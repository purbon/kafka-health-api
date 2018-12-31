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
      assert( color.get  == JsString("Green") )
    }
  }

}
