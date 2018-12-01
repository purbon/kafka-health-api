package models

import com.typesafe.config.{ConfigList, ConfigValue}
import javax.inject.Inject
import collection.JavaConverters._

class Configuration  @Inject() (config: play.api.Configuration) {

  val servers: Seq[String] = config.get[ConfigList]("bootstrap.servers")
    .asScala
    .map(_.unwrapped().toString)

}

case class KafkaBrokerConfigDesc(brokerId: String, config: List[KafkaConfigEntry])
case class KafkaConfigDescription(entries: List[KafkaBrokerConfigDesc])
case class KafkaConfigEntry(name: String, value: String)