package controllers

import com.salesforce.kafka.test.{KafkaTestCluster, KafkaTestUtils}
import org.apache.kafka.clients.admin.AdminClient
import org.scalatest.{BeforeAndAfterAll, TestData}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsLookupResult, JsString}
import play.api.test.Helpers._
import play.api.test._

class StatusControllerSpec extends PlaySpec with GuiceOneAppPerTest
                                            with Injecting with BeforeAndAfterAll {

  var kafkaTestCluster:KafkaTestCluster = _

  override def beforeAll() {
    kafkaTestCluster = new KafkaTestCluster(1)
    kafkaTestCluster.start

  }

  override def afterAll() {
    kafkaTestCluster.stop
  }

  implicit override def newAppForTest(testData: TestData): Application = {
    val serversList: String = kafkaTestCluster.getKafkaConnectString

    val utils = new KafkaTestUtils(kafkaTestCluster)

    new GuiceApplicationBuilder()
      .configure(Map("bootstrap.servers" -> List(serversList)))
      .overrides(bind[AdminClient].toInstance(utils.getAdminClient))
      .build()
  }

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
