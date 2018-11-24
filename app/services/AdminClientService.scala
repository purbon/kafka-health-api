package services

import javax.inject.Inject
import models.Configuration
import org.apache.kafka.clients.admin.AdminClient

import scala.collection.JavaConverters._
import scala.collection.mutable._

class AdminClientService @Inject() (config: Configuration) {

  val adminConf: Map[String, AnyRef] = HashMap("bootstrap.servers" -> config.servers.mkString(","))

  val adminClient = AdminClient.create(adminConf.asJava)

  def isr(): Boolean = {

    val topicNames = adminClient.listTopics().names().get()

    adminClient
      .describeTopics(topicNames)
      .values()
      .asScala
      .map {
        case (_, descriptionFuture) => {
          val topicDescription = descriptionFuture.get()
          topicDescription
            .partitions()
            .asScala
            .map { partitionInfo =>
              partitionInfo.isr().size() == partitionInfo.replicas().size()
            }
            .foldLeft[Boolean](true) ( (a,v) => a & v )

        }
      }
      .foldLeft[Boolean](true) ( (a,v) => a & v )
  }

  def close(): Unit = {
    adminClient.close()
  }
}
