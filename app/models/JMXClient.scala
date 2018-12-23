package models

import java.util

import javax.management.{MBeanServerConnection, ObjectInstance, ObjectName}
import javax.management.remote.{JMXConnector, JMXConnectorFactory, JMXServiceURL}

import scala.collection.JavaConverters._

case class JMXAttribute(name: String, value: AnyRef)

object JMXClient {

  def apply(jMXConnector: JMXConnector): JMXClient = new JMXClient(jMXConnector)

  def build(port: Int):JMXClient = {

    val  url = new JMXServiceURL(s"service:jmx:rmi:///jndi/rmi://:$port/jmxrmi")
    val jMXConnector = JMXConnectorFactory.connect(url, null)
    JMXClient(jMXConnector)
  }
}

class JMXClient(jMXConnector: JMXConnector) {

  private val mbsc: MBeanServerConnection = jMXConnector.getMBeanServerConnection

  val instances: util.Set[ObjectInstance] = mbsc.queryMBeans(null, null)

  def checkBean(metricBean: String): Seq[JMXAttribute] = {
    val objectName: ObjectName = new ObjectName(metricBean)
    val attributeNames: Array[String] = mbsc.getMBeanInfo(objectName).getAttributes.map(_.getName)

    mbsc.getAttributes(objectName, attributeNames)
      .asList
      .asScala
      .map(attr => JMXAttribute(attr.getName, attr.getValue))
  }

}

