package services

import models.JMXClient
import utils.KafkaPlaySpec

class MetricServiceSpec extends KafkaPlaySpec {

  "MetricService" should {

    "pull metrics" in {

      //kafkaTestUtils.createTopic("mytopic", 2, 1)

      val metricName = "kafka.server:type=ReplicaManager,name=PartitionCount"
      val client = JMXClient.build(9999)

      val attrs = client.checkBean(metricName)

      assert(attrs.size > 0)

    }
  }

}
