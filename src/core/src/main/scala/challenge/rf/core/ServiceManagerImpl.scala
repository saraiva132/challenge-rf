package challenge.rf.core

import challenge.rf.api._
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

class ServiceManagerImpl(config: Vector[ServiceMetadata]) extends ServiceManager {

  /* Efficient hashmap that handles concurrent accesses for free */
  private val services = TrieMap.empty[String, (ServiceState, Service, Thread)]
  /* Load config. Initially there are no services active so lets just put the entries null and the state NEW */
  config.foreach(it => services.put(it.name, (ServiceState(), null, null)))

  override def start(name: String): Result = {
    services.get(name) match {
      case Some((state, service, thread)) => state.state match {
        case NEW | DEAD | STARTING =>
          val svConf = config.find(_.name equals name).get /* Safe to call get here*/
          if (svConf.dependencies.exists(services.get(_).get._1.state != RUNNING)) return NOK /* Safe to call get here*/
          Future {
            Try {
              state.synchronized {
                if (services.get(name).get._1 != RUNNING) {
                  val service = Class.forName(svConf.cls).asSubclass(classOf[Service]).newInstance()
                  state.state = STARTING
                  services.update(name, (state, service, null))
                  service.start()
                  val t = new Thread(service)
                  state.state = RUNNING
                  services.update(name, (state, service, t))
                  t.start()
                }
              }
            }.getOrElse(NOK)
          } // deal with result..
          OK
        case RUNNING | STOPPING => OK
      }
      case None => NOK
    }
  }

  override def stop(name: String): Result = {
    services.get(name) match {
      case Some((state, service, thread)) => state.state match {
        case RUNNING =>
          Future {
            state.synchronized {
              if (services.get(name).get._1.state == RUNNING) {
                state.state = STOPPING
                services.update(name, (state, service, thread))
                service.stop()
                thread.stop()
                state.state = DEAD
                services.update(name, (state, null, null))
              }
            }
          }
          OK
        case _ => NOK
      }
    }
  }

  override def stopWithDependencies(name: String): Result = ???

  override def stopAll(): Unit = ???

  override def startWithDependencies(name: String): Result = {
    services.get(name) match {
      case Some((state, service, thread)) => state.state match {
        case NEW | DEAD => OK
        case STARTING | RUNNING | STOPPING => NOK
      }
      case None => NOK
    }
  }

  override def startAll(): Unit = ???

  /**
   * Yet another recursive method
   *
   * @param deps
   * @param state
   */
  private def validateDependenciesState(deps : Vector[String], state : State) = {

  }
}
