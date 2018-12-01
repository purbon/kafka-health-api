package services

import javax.inject.Inject

import org.apache.kafka.clients.admin.{AdminClient, ConfigEntry}
import org.apache.kafka.common.config.ConfigResource

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


  def clusterConfig(filters : Option[List[String]] = None): Map[String, List[ConfigEntry]] = {

    val configResources = adminClient
      .describeCluster()
      .nodes.get
      .asScala
      .map( node => new ConfigResource(ConfigResource.Type.BROKER, node.idString()))
      .toList

    listConfigValues(configResources, filters)
  }

  def close(): Unit = {
    adminClient.close()
  }

  private def listConfigValues(resources: List[ConfigResource],
                               filters: Option[List[String]] = None): Map[String, List[ConfigEntry]] = {

    adminClient
      .describeConfigs(resources.asJavaCollection)
      .all.get
      .asScala
      .map {
        case (resource, config) => {
          ( resource.name(),
            config
              .entries()
              .asScala
              .toList
              .filterNot( p => filters.exists( list => !list.contains(p.name()) ))
          )
        }
      }.toMap
  }
}
