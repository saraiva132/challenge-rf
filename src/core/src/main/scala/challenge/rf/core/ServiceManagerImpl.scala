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
      case Some((svState, service, thread)) =>
        svState.synchronized {
          svState.state match {
            case NEW | DEAD | STOPPING =>
              // Correct inconsistent states while holding the lock
              val depsState = fromConfig(name).get.dependencies.
                filter(services.get(_).get._1.state != RUNNING)

              if (withDeps) depsState.foreach(start(_, withDeps = true))
              else if (depsState.size > 0) return NOK

              Try {
                val svConf = fromConfig(name).get /* Safe to call get here*/
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
            case RUNNING | STARTING =>
              OK
          }
        }
      case None => NOK
    }
  }

  override def stop(name: String, withDeps: Boolean = false): Result = {
    services.get(name) match {
      case Some((svState, service, thread)) =>
        svState.synchronized {
          svState.state match {
            case RUNNING =>
              svState.state = STOPPING
              service.stop()
              try {
                thread.join(3000)
              } finally {
                if (thread.isAlive)
                  thread.stop() // Let the service have a change to shutdown
              }
              svState.state = DEAD
              services.update(name, (svState, null, null))
              OK
            case STARTING => OK
            case NEW | DEAD | STOPPING => NOK
          }
        }
      case None => NOK
    }
  }

  override def stopWithDependencies(name: String): Result = {
    fromConfig(name) match {
      case Some(serviceMetadata) =>
        def loopDeps(metadata: ServiceMetadata): Unit = {
          val results = config.filter(_.dependencies.
            filter(services.get(_).get._1.state == RUNNING).  // Filter all deps which are running
            exists(_ equals metadata.name)). //filter by only dependant services
            map(metadata => Future { loopDeps(metadata)}) // map to future recursive call

          if (results.size > 0) {
            Future.sequence(results) onComplete {
              case _ => Future {
                stop(metadata.name, true)
              }
            }
          }
          else stop(metadata.name, true)
        }

        loopDeps(serviceMetadata)
        OK
      case None => NOK
    }
  }

  override def startWithDependencies(name: String): Result = {
    fromConfig(name) match {
      case Some(serviceMetadata) =>
        def loopDeps(metadata: ServiceMetadata): Unit = {
          val results = metadata.dependencies.
            filter(services.get(_).get._1.state != RUNNING).
            flatMap(fromConfig(_)).
            map(metadata => Future {
              loopDeps(metadata)
            })
          if (results.size > 0) {
            Future.sequence(results) onComplete {
              case _ => Future {
                start(metadata.name, true)
              }
            }
          }
          else start(metadata.name, true)
        }
        loopDeps(serviceMetadata)
        OK
      case None => NOK
    }
  }

  override def stopAll(): Unit = config.
    foreach(svMetadata => stopWithDependencies(svMetadata.name))

  override def startAll(): Unit = config.
    foreach(svMetadata => startWithDependencies(svMetadata.name))
}