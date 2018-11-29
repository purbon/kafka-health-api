package controllers

import javax.inject._
import models.{Health, KafkaStatus}
import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import services.KafkaService


@Singleton
class StatusController @Inject()(cc: ControllerComponents,
                                 kafkaService: KafkaService,
                                 config: Configuration) extends AbstractController(cc) {

  implicit val statusWrites: Writes[KafkaStatus] = (
    (JsPath \ "semaphore").write[String] and
      (JsPath \ "errors").write[Set[String]]
    )(unlift(KafkaStatus.unapply))

  implicit val healthWrites: Writes[Health] = (
    (JsPath \ "status").write[KafkaStatus] and
      (JsPath \ "time").write[Long]
    )(unlift(Health.unapply))


  def health() = Action {

    val health = Health(
                      status = kafkaService.status(),
                      time = System.currentTimeMillis())
    Ok(Json.toJson(health))
  }
}