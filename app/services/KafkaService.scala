package services

import java.net.Socket

import javax.inject.Inject
import models.{Color, Configuration, KafkaStatus, KafkaStatusErrors}


class KafkaService  @Inject() (config: Configuration,
                               adminClientService: AdminClientService) {


  def status(): KafkaStatus = {

    val aliveServers:Int = alive(config.servers)

    if (aliveServers == config.servers.size) {
      if (adminClientService.isr()) {
        KafkaStatus(Color.Green, Set.empty)
      } else {
        KafkaStatus(Color.Orange, Set(KafkaStatusErrors.isrError))
      }
    }
    else if (aliveServers > config.servers.size*0.5) {
      KafkaStatus(Color.Orange, Set(KafkaStatusErrors.SomeKafkaBrokersAreUnreachable))
    }
    else {
      KafkaStatus(Color.Red, Set(KafkaStatusErrors.LessThanXBrokersAreUnreachable))
    }
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
