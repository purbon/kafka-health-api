package services

import javax.inject.Inject
import models.Configuration
import org.apache.kafka.clients.admin.AdminClient

import scala.collection.JavaConverters._

class AdminClientService @Inject() (adminClient: AdminClient) {

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
