package challenge.rf.api

/**
 * Service Manager API
 */
trait ServiceManager {
  def start(name : String)
  def startWithDependencies(name : String)
  def stop(name : String)
  def stopWithDependencies(name : String)
  def startAll()
  def stopAll()
}

trait Service {
  def start() : Result
  def stop() : Result
}

case class ServiceMetadata(name : String, dependencies : Vector[String], state : State = INIT)

trait ServiceLoader {
  def load(file : String) : Vector[ServiceMetadata]
  def validate(metadata :Vector[ServiceMetadata]) : Result
  def loadAndValidate(file : String) : Vector[ServiceMetadata]
}

