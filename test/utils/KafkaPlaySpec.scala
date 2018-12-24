package utils

import java.util.concurrent.TimeUnit

import com.salesforce.kafka.test.{KafkaTestCluster, KafkaTestUtils}
import org.apache.kafka.clients.admin.AdminClient
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, TestData}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting

import scala.collection.JavaConverters._

abstract class KafkaPlaySpec extends PlaySpec
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with GuiceOneAppPerTest
  with Injecting {

  var kafkaTestCluster:KafkaTestCluster = _
  var kafkaTestUtils: KafkaTestUtils = _

  var adminClient: AdminClient = _

  override def beforeAll() {
    kafkaTestCluster = new KafkaTestCluster(1)
    kafkaTestCluster.start
    kafkaTestUtils = new KafkaTestUtils(kafkaTestCluster)

  }

  override def beforeEach() =  {
  }

  override  def afterEach() = (

  )

  override def afterAll() {
    kafkaTestCluster.stop
    adminClient.close
  }

  implicit override def newAppForTest(testData: TestData): Application = {
    val serversList: String = "localhost:9092"  // kafkaTestCluster.getKafkaConnectString

    val utils = new KafkaTestUtils(kafkaTestCluster)
    kafkaTestCluster.getKafkaBrokers.asList().asScala.foreach { broker =>
      utils.waitForBrokerToComeOnLine(broker.getBrokerId, 1, TimeUnit.MINUTES)
    }

    adminClient = AdminClient.create(buildDefaultClientConfig(serversList))

    new GuiceApplicationBuilder()
      .configure(Map("bootstrap.servers" -> List(serversList)))
      .overrides(bind[AdminClient].toInstance(adminClient))
      .build()
  }

  private def buildDefaultClientConfig(kafkaConnectString: String): java.util.Map[String, AnyRef] = {
    mapAsJavaMap(Map("bootstrap.servers" -> kafkaConnectString, "client.id" -> "testAdminClient"))
  }
}
