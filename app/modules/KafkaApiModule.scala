package modules

import com.google.inject.AbstractModule
import com.typesafe.config.ConfigList
import models.JMXClient
import org.apache.kafka.clients.admin.AdminClient
import play.api.{Configuration, Environment}

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap

class KafkaApiModule(env: Environment,
                     config: Configuration) extends AbstractModule {

  override def configure(): Unit = {

    val adminConf: Map[String, AnyRef] = HashMap("bootstrap.servers" -> servers.mkString(","))
    val adminClient: AdminClient = AdminClient.create(adminConf.asJava)

    bind(classOf[AdminClient]).toInstance(adminClient)

    val jmxPort = config.get[Int]("jmx.port")
    val jmxClient = JMXClient(jmxPort)
    bind(classOf[JMXClient]).toInstance(jmxClient)

  }

  private def servers = {
    config.get[ConfigList]("bootstrap.servers")
      .asScala
      .map(_.unwrapped().toString)
  }
}
