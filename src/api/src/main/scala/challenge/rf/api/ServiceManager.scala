package challenge.rf.api

import scala.concurrent.Future

/**
 * Service Manager API
 */
trait ServiceManager {
  def start(name : String) : Result
  def startWithDependencies(name : String) : Result
  def stop(name : String)  : Result
  def stopWithDependencies(name : String) : Result
  def startAll()
  def stopAll()
}

trait Service extends Runnable{
  def start() : Result
  def stop() : Result
}

case class ServiceMetadata(name : String, cls : String, dependencies : Vector[String] = Vector.empty)

/**
 * Allow Service to mutate its own state while keeping the same object. This is useful because the synchronization
 * will all be done on the ServiceState object. All mutations MUST be done inside a synchronized block.
 *
 * @param state - Initial State.
 */
case class ServiceState(var state : State = NEW)

trait ServiceLoader {
  def load(file : String) : Vector[ServiceMetadata]
  def validate(metadata :Vector[ServiceMetadata]) : Result
  def loadAndValidate(file : String) : Option[Vector[ServiceMetadata]]
}

