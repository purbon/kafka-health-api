package models


object Color {
  val Red = "Red"
  val Orange = "Orange"
  val Green = "Green"
}

object KafkaStatusErrors {
  val LessThanXBrokersAreUnreachable = "Less than the configured minimum of Kafka brokers are unreachable, verify your connection"
  val SomeKafkaBrokersAreUnreachable = "There are Kafka Brokers not reachable, check the network connection"
  val isrError = "There are topics with not all partitions in sync"

}
case class KafkaStatus(semaphore: String, errors: Set[String])
case class Health(hello: String, status: KafkaStatus, time: Long)
