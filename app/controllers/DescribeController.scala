package controllers

import javax.inject._

import models.{KafkaBrokerConfigDesc, KafkaConfigDescription, KafkaConfigEntry}
import play.api.libs.json._
import play.api.mvc._
import services.KafkaService


@Singleton
class DescribeController @Inject()(cc: ControllerComponents, kafkaService: KafkaService )
  extends AbstractController(cc)
    with JsonWritersContext {

  def kafkaBrokerConfig() = Action {

    val kafkaConfig = kafkaService.clusterConfig()

    Ok(Json.toJson(kafkaConfig))
  }
}
