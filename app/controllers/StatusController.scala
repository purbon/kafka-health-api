package controllers

import javax.inject._
import models._
import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import services.KafkaService


@Singleton
class StatusController @Inject()(cc: ControllerComponents,
                                 kafkaService: KafkaService,
                                 config: Configuration) extends AbstractController(cc)
                                 with JsonWritersContext {

  implicit val statusWrites: Writes[KafkaStatus] = (
    (JsPath \ "semaphore").write[String] and
      (JsPath \ "errors").write[Set[String]]
    )(unlift(KafkaStatus.unapply))

  implicit val healthWrites: Writes[Health] = (
      (JsPath \ "hello").write[String] and
      (JsPath \ "versions").write[KafkaConfigDescription] and
      (JsPath \ "status").write[KafkaStatus] and
      (JsPath \ "time").write[Long]
    )(unlift(Health.unapply))

  implicit val replicaStatusWrites: Writes[ReplicaStatus] = (
    (JsPath \ "partitionName").write[String] and
      (JsPath \ "isrList").write[List[String]] and
      (JsPath \ "replicaList").write[List[String]] and
      (JsPath \ "inSync").write[Boolean]
    )(unlift(ReplicaStatus.unapply))

  implicit val brokerStatusWrites: Writes[BrokerStatus] = (
    (JsPath \ "allInSyncReplicas").write[Boolean] and
      (JsPath \ "replicaList").write[List[ReplicaStatus]]
    )(unlift(BrokerStatus.unapply))


  implicit val guarantiesWrites: Writes[Guaranties] = (
    (JsPath \ "producer").write[String] and
      (JsPath \ "broker").write[BrokerStatus]
    )(unlift(Guaranties.unapply))


  def health() = Action {

    val health = Health(
                      hello = "Welcome to the Kafka Health API",
                      versions = kafkaService.clusterProtocolVersions(),
                      status = kafkaService.status(),
                      time = System.currentTimeMillis())
    Ok(Json.toJson(health))
  }

  def clusterGuaranties() = Action {

    val producerGuaranties = kafkaService.iamUsingFullGuaranties()

    val guaranties = Guaranties( producer = s"$producerGuaranties",
                                 broker = BrokerStatus(
                                   kafkaService.allInSyncReplicas(),
                                   kafkaService.replicaList()
                                 )
    )
    Ok(Json.toJson(guaranties))
  }

}
