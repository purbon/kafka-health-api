package services

import javax.inject.Inject
import models.JMXClient

class MetricService @Inject()(jmxClient: JMXClient) {

  def isAckAll(): Boolean = {
    val metric = "kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Produce"

    val attrs = jmxClient.checkBean(metric)

    attrs.size > 0 && attrs.head.value.asInstanceOf[Double] > 0
  }

}
