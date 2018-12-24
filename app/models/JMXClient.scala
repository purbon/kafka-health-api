package models

import java.io.IOException

import javax.management.ObjectName
import javax.management.remote.{JMXConnector, JMXConnectorFactory, JMXServiceURL}

import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}

case class JMXAttribute(name: String, value: AnyRef)

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object JMXClient {

  def apply(port: Int) : JMXClient = {
    val  url = new JMXServiceURL(s"service:jmx:rmi:///jndi/rmi://:$port/jmxrmi")
    new JMXClient(url)
  }
}

class JMXClient(url: JMXServiceURL) {


  def checkBean(metricBean: String): Seq[JMXAttribute] = {

    val futureBeanValue: Future[List[JMXAttribute]] = connectionWithRetry.map { jmxConnector =>
      retrieveObjectName(jmxConnector, metricBean)
    }
    Await.result(futureBeanValue, 1 minutes)
  }

  private def retrieveObjectName(jmxConnector: JMXConnector, metricBean: String): List[JMXAttribute] = {

    val beanServerConnection = jmxConnector.getMBeanServerConnection
    val objectName: ObjectName = new ObjectName(metricBean)
    val attributeNames: Array[String] = beanServerConnection.getMBeanInfo(objectName).getAttributes.map(_.getName)

    beanServerConnection.getAttributes(objectName, attributeNames)
      .asList
      .asScala
      .map(attr => JMXAttribute(attr.getName, attr.getValue))
      .toList
  }

  private def connectionWithRetry(): Future[JMXConnector] = {
    connection()
      .recoverWith({
        case ioEx: IOException => connection()
        case ex => throw ex
      })
  }

  private def connection(): Future[JMXConnector] = {
    Future {
      JMXConnectorFactory.connect(url, null)
    }
  }
}
