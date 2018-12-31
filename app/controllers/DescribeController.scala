package controllers

import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import javax.inject._
import models.KafkaConfigDescription
import play.api.libs.json._
import play.api.mvc._
import services.KafkaService


@Singleton
@Api class DescribeController @Inject()(cc: ControllerComponents, kafkaService: KafkaService )
  extends AbstractController(cc)
    with JsonWritersContext {

  @ApiResponses(Array(
    new ApiResponse(code = 200,
                    message = "Kafka Cluster config successfully retrieved")
  ))
  def kafkaBrokerConfig() = Action {

    val kafkaConfig: KafkaConfigDescription = kafkaService.clusterConfig()

    Ok(Json.toJson(kafkaConfig))
  }
}
