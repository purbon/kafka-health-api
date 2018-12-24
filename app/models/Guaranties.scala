package models

case class BrokerStatus(allInSyncReplicas: Boolean,
                        replicaList: List[ReplicaStatus])

case class ReplicaStatus(partitionName: String, isrList: List[String], replicaList: List[String], inSync: Boolean)

case class Guaranties(producer: String, broker: BrokerStatus)