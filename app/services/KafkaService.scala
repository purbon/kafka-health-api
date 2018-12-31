package services

import java.net.Socket

import javax.inject.Inject
import models._
import org.apache.kafka.common.config.ConfigResource


class KafkaService  @Inject() (appConfig: Configuration,
                               adminClientService: AdminClientService,
                               metricService: MetricService) {

  /**
    * Check the Kafka status
    *
    * @return KafkaStatus with the color status and a simple error description
    */
  def status(): KafkaStatus = {

    val aliveServers:Int = alive(appConfig.servers)
    val numberOfServers:Int = appConfig.servers.size

    if (aliveServers == numberOfServers) {
      if (adminClientService.isr()) {
        KafkaStatus(Color.Green, Set.empty)
      } else {
        KafkaStatus(Color.Orange, Set(KafkaStatusErrors.isrError))
      }
    }
    else if (aliveServers > numberOfServers*0.5) {
      KafkaStatus(Color.Orange, Set(KafkaStatusErrors.SomeKafkaBrokersAreUnreachable))
    }
    else {
      KafkaStatus(Color.Red, Set(KafkaStatusErrors.LessThanXBrokersAreUnreachable))
    }
  }

  def allInSyncReplicas(): Boolean = {
    adminClientService.isr()
  }

  def replicaList(): List[ReplicaStatus] = {
    adminClientService.replicaLists()
  }

  def iamUsingFullGuaranties(): Boolean = {
    metricService.isAckAll()
  }

  def clusterConfig(): KafkaConfigDescription = {
    getClusterConfigValues()
  }

  def clusterProtocolVersions(): KafkaConfigDescription = {
    getClusterConfigValues(filters = Some(List("log.message.format.version")))
  }

  private def getClusterConfigValues(configType: ConfigResource.Type = ConfigResource.Type.BROKER, filters : Option[List[String]] = None): KafkaConfigDescription = {
    val kafkaConfig: List[KafkaBrokerConfigDesc]= adminClientService
      .clusterConfig(configType, filters)
      .map {
        case (brokerId, configList) => {
          KafkaBrokerConfigDesc(
            brokerId = brokerId,
            config = configList
              .map { entry =>
                KafkaConfigEntry(name = entry.name(), value = entry.value())
              }
          )
        }
      }.toList

    KafkaConfigDescription(kafkaConfig)
  }

  private def ping(host: String, port:String): Unit = {
    val socket = new Socket(host, port.toInt)

    val os = socket.getOutputStream

    os.write(1)

  }

  private def alive(servers: Seq[String]): Int = {
    servers.foldLeft[Int](0) { (acc, url) =>
      val uri = url.intern().split(":")
      try {
        ping(uri(0), uri(1))
        acc+1
      } catch {
        case e: Exception => acc
      }
    }
  }

}
