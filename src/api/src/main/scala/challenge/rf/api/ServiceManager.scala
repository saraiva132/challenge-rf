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

case class ServiceMetadata(name : String, cls : String, dependencies : Vector[String] = Vector.empty, state : State = NEW)

trait ServiceLoader {
  def load(file : String) : Vector[ServiceMetadata]
  def validate(metadata :Vector[ServiceMetadata]) : Result
  def loadAndValidate(file : String) : Option[Vector[ServiceMetadata]]
}

