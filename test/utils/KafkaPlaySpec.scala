package utils

import com.salesforce.kafka.test.{KafkaTestCluster, KafkaTestUtils}
import org.apache.kafka.clients.admin.AdminClient
import org.scalatest.{BeforeAndAfterAll, TestData}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting

abstract class KafkaPlaySpec extends PlaySpec
  with BeforeAndAfterAll
  with GuiceOneAppPerTest
  with Injecting {

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
}
