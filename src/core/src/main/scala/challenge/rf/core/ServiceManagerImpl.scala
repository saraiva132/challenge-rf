package challenge.rf.core

import challenge.rf.api._
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

class ServiceManagerImpl(config: Vector[ServiceMetadata]) extends ServiceManager {

  /* Efficient hashmap that handles concurrent accesses for free */
  private val services = TrieMap.empty[String, (ServiceState, Service, Thread)]
  /* Load config. Initially there are no services active so lets just put the entries null and the state NEW */
  config.foreach(it => services.put(it.name, (ServiceState(), null, null)))

  private val fromConfig = (name: String) => config.find(_.name equals name)

  override def start(name: String, withDeps: Boolean = false): Result = {
    services.get(name) match {
      case Some((svState, service, thread)) => Future {
        svState.synchronized {
          svState.state match {
            case NEW | DEAD | STARTING | STOPPING =>
              val svConf = fromConfig(name).get /* Safe to call get here*/
              if (svConf.dependencies.exists(services.get(_).get._1.state != RUNNING)) return NOK /* Safe to call get here*/
              Try {
                val service = Class.forName(svConf.cls).asSubclass(classOf[Service]).newInstance()
                svState.state = STARTING
                services.update(name, (svState, service, null))
                val result = service.start()
                val t = new Thread(service)
                t.setDaemon(true)
                t.start()
                svState.state = RUNNING
                services.update(name, (svState, service, t))
                result
              }.getOrElse(NOK) // deal with result..
            case RUNNING =>
              OK
          }
        }
      }
        OK
      case None => NOK
    }
  }

  override def stop(name: String, withDeps: Boolean = false): Result = {
    services.get(name) match {
      case Some((state, service, thread)) => state.state match {
        case RUNNING | STARTING =>
          Future {
            state.synchronized {
              if (services.get(name).get._1.state == RUNNING) {
                state.state = STOPPING
                services.update(name, (state, service, thread))
                service.stop()
                try {
                  thread.join(3000)
                } finally{
                  if(thread.isAlive)
                    thread.stop() // Let the service have a change to shutdown
                }
                state.state = DEAD
                services.update(name, (state, null, null))
              }
            }
          }
          OK
        case NEW | DEAD | STOPPING => NOK
      }
    }
  }

  override def stopWithDependencies(name: String): Result = {
    fromConfig(name) match {
      case Some(serviceMetadata) =>
        def loopDeps(metadata: ServiceMetadata): Unit = {
          val deps = config.flatMap(_.dependencies. // Find all dependencies
            filter(_ equals metadata.name)). // Filter all dependant services
            filter(services.get(_).get._1 == RUNNING) // Filter all which are running
          deps.flatMap(fromConfig(_)).foreach(loopDeps) // Recursive call
          stop(metadata.name) //finally stop
        }
        loopDeps(serviceMetadata)
        stop(serviceMetadata.name)
        OK
      case None => NOK
    }
  }

  override def startWithDependencies(name: String): Result = {
    fromConfig(name) match {
      case Some(serviceMetadata) =>
        def loopDeps(metadata: ServiceMetadata): Unit = {
          val deps = metadata.dependencies.filter(services.get(_).get._1.state != RUNNING)
          deps.flatMap(fromConfig(_)).foreach(loopDeps)
          start(metadata.name)
        }
        loopDeps(serviceMetadata)
        start(serviceMetadata.name)
        OK
      case None => NOK
    }
  }

  override def stopAll(): Unit = config.
    foreach(svMetadata => stopWithDependencies(svMetadata.name))

  override def startAll(): Unit = config.
    foreach(svMetadata => startWithDependencies(svMetadata.name))
}
