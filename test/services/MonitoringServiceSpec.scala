package controllers

import models.JMXClient
import utils.KafkaPlaySpec

class MonitoringServiceSpec extends KafkaPlaySpec {

  "Kafka Monitoring JMX api" should {
    "fetch jmx metrics" in {
      val metricBean = "kafka.server:type=KafkaServer,name=BrokerState"
      val client = JMXClient(5678)
      assert( client.checkBean(metricBean).size > 0 )
    }
  }
}
