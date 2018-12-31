package controllers

import com.salesforce.kafka.test.{KafkaTestCluster, KafkaTestUtils}
import models.JMXClient
import org.apache.kafka.clients.admin.AdminClient
import org.scalatest.{BeforeAndAfterAll, TestData}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

class MonitoringServiceSpec extends PlaySpec
                            with GuiceOneAppPerTest
                            with Injecting
                            with BeforeAndAfterAll {

  var kafkaTestCluster:KafkaTestCluster = _

  override def beforeAll() {
    kafkaTestCluster = new KafkaTestCluster(1)
    kafkaTestCluster.start
  }

  override def afterAll() = {
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

  "Kafka Monitoring JMX api" should {
    "fetch jmx metrics" in {
      println(sys.env)
      val metricBean = "kafka.server:type=KafkaServer,name=BrokerState"
      val client = JMXClient(5678)
      assert( client.checkBean(metricBean).size > 0 )
    }
  }
}
