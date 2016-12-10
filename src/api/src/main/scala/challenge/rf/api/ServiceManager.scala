package challenge.rf.api

import scala.concurrent.Future

/**
 * Service Manager API
 */
trait ServiceManager {
  def start(name : String, withDeps : Boolean) : Result
  def startWithDependencies(name : String) : Result
  def stop(name : String, withDeps : Boolean)  : Result
  def stopWithDependencies(name : String) : Result
  def startAll()
  def stopAll()
}

/**
 * Service API
 */
trait Service extends Runnable{
  def start() : Result
  def stop() : Result
}

/**
 * Service Metadata. Will be parsed from a json document.
 *
 * @param name
 * @param cls
 * @param dependencies
 */
case class ServiceMetadata(name : String, cls : String, dependencies : Vector[String] = Vector.empty)

/**
 * Allows Service to mutate its own state while keeping the same object (for locking purposes). This is useful because the synchronization
 * will all be done on the ServiceState object. All mutations MUST be done inside a synchronized block.
 *
 * @param state - Initial State.
 */
case class ServiceState(var state : State = NEW)

/**
 * Loads and validates service metadata. With Scala and Java API.
 */
trait ServiceLoader {
  def load(file : String) : Vector[ServiceMetadata]
  def validate(metadata :Vector[ServiceMetadata]) : Result
  def loadAndValidate(file : String) : Option[Vector[ServiceMetadata]]
}

