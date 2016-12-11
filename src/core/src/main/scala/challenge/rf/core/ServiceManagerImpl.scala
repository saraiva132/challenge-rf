package challenge.rf.core

import java.util.concurrent.atomic.AtomicBoolean

import challenge.rf.api._
import scala.collection.concurrent.TrieMap
import scala.concurrent.{Promise, Future}
import scala.util.{Try, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class ServiceManagerImpl(config: Vector[ServiceMetadata]) extends ServiceManager {

  type Override = AtomicBoolean
  /* Efficient hashmap that handles concurrent accesses for free */
  private val services = TrieMap.empty[String, (ServiceState, Service, Thread, Override)]
  /* Load config. Initially there are no services active so lets just put the entries null and the state NEW */
  config.foreach(it => services.put(it.name, (ServiceState(), null, null, new AtomicBoolean(false))))

  private val fromConfig = (name: String) => config.find(_.name equals name)

  override def start(name: String, withDeps: Boolean = false): Result = {
    services.get(name) match {
      case Some((svState, sv, thread, ow)) =>
        svState.synchronized {
          svState.state match {
            case NEW | DEAD | STOPPING =>
              if (!withDeps && fromConfig(name).get.dependencies.
                filter(services.get(_).get._1.state != RUNNING).
                size > 0)
                return NOK
              Try {
                val svConf = fromConfig(name).get /* Safe to call get here*/
                val service = {
                  if (svState.state == NEW)
                    Class.forName(svConf.cls).asSubclass(classOf[Service]).newInstance()
                  else sv
                }
                svState.state = STARTING
                services.update(name, (svState, service, null, ow))
                val result = service.start()
                if (ow.get()) {
                  service.stop()
                  svState.state = DEAD
                  return NOK
                }
                val t = new Thread(service)
                t.setDaemon(true)
                t.start()
                svState.state = RUNNING
                services.update(name, (svState, service, t, ow))
                result
              }.recover { case _ =>
                svState.state = DISABLED
                NOK
              }.get // deal with result..
            case RUNNING | STARTING =>
              OK
            case DISABLED =>
              NOK
          }
        }
      case None => NOK
    }
  }

  override def stop(name: String, withDeps: Boolean = false): Result = {
    services.get(name) match {
      case Some((svState, service, thread, ow)) =>
        ow.set(true)
        val res = svState.synchronized {
          svState.state match {
            case RUNNING | STARTING =>

              if (!withDeps && (config.filter(_.dependencies.exists(_ equals name)).
                flatMap(it => services.get(it.name)).
                filter(_._1.state == RUNNING).size > 0)) {
                return NOK
              }

              svState.state = STOPPING
              service.stop()
              try {
                thread.join(3000)
              } finally {
                if (thread.isAlive)
                  thread.stop() // Let the service have a chance to shutdown
              }
              svState.state = DEAD
              services.update(name, (svState, service, null, ow))
              OK
            case NEW | DEAD | STOPPING | DISABLED => OK
          }
        }
        ow.set(false)
        res
      case None => NOK
    }
  }

  override def stopWithDependencies(name: String): Result = {
    fromConfig(name) match {
      case Some(serviceMetadata) =>
        def loopDeps(metadata: ServiceMetadata): Future[Result] = {
          val p = Promise[Result]

          Future {
            val results = config.filter(_.dependencies.
            filter(services.get(_).get._1.state == RUNNING).  // Filter all deps which are running
            exists(_ equals metadata.name)). //filter by only dependant services
            map(loopDeps(_)) // map to future recursive call

            Future.sequence(results) onComplete {
              case Success(res) =>
                if (res.forall(_ equals OK)) {
                  stop(metadata.name, true)
                  p.success(OK)
                }
                else p.success(NOK)
              case _ => p.success(NOK)
            }
          }
          p.future
        }

        loopDeps(serviceMetadata)
        OK
      case None => NOK
    }
  }

  override def startWithDependencies(name: String): Result = {
    fromConfig(name) match {
      case Some(serviceMetadata) =>
        def loopDeps(metadata: ServiceMetadata): Future[Result] = {

          val p = Promise[Result]

          Future {
            val results = metadata.dependencies.
              filter(services.get(_).get._1.state != RUNNING).
              flatMap(fromConfig(_)).
              map(loopDeps(_))

            Future.sequence(results) onComplete {
              case Success(res) =>
                if (res.forall(_ equals OK)) {
                  val res = start(metadata.name, true)
                  p.success(res)
                }
                else p.success(NOK)
              case _ => p.success(NOK)
            }
          }
          p.future
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

  def activeServices(): Vector[ServiceMetadata] =
    services.filter { case (k, v) => v._1.state == RUNNING }.
      flatMap { case (k, v) => fromConfig(k) }.toVector

  def disabledServices(): Vector[ServiceMetadata] =
    services.filter { case (k, v) => v._1.state == DISABLED }.
      flatMap { case (k, v) => fromConfig(k) }.toVector
}