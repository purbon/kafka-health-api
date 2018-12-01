package controllers

import models.{KafkaBrokerConfigDesc, KafkaConfigDescription, KafkaConfigEntry}
import play.api.libs.json.{Json, Writes}

trait JsonWritersContext {

  implicit val configEntryWrites = new Writes[KafkaConfigEntry] {
    def writes(configEntry: KafkaConfigEntry) = Json.obj(
      "name" -> configEntry.name,
      "value" -> configEntry.value
    )
  }

  implicit val brokerConfigWrites = new Writes[KafkaBrokerConfigDesc] {
    def writes(brokerConfig: KafkaBrokerConfigDesc) = Json.obj(
      "brokerId" -> brokerConfig.brokerId,
      "config" -> brokerConfig.config
    )
  }

  implicit val configWrites = new Writes[KafkaConfigDescription] {
    def writes(configDesc: KafkaConfigDescription) = Json.obj(
      "brokersConfig" -> configDesc.entries
    )
  }
}
