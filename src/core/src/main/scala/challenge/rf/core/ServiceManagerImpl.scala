package challenge.rf.core

import challenge.rf.api.{ServiceMetadata, ServiceManager}

class ServiceManagerImpl(config : Vector[ServiceMetadata]) extends ServiceManager{

  val metadata = config.map(it => (it.name,it)).toMap

  override def start(name: String): Unit = metadata.get(name) match {
    case Some(service) =>
    case None =>
  }

  override def stop(name: String): Unit = ???

  override def stopWithDependencies(name: String): Unit = {}

  override def stopAll(): Unit = ???

  override def startWithDependencies(name: String): Unit = ???

  override def startAll(): Unit = ???
}
