package challenge.rf.api

trait ServiceManager {
  def start(name : String)
  def startWithDependencies(name : String)
  def stop(name : String)
  def startAll()
  def stopAll()
}

trait Service {
  def start() : Result
  def stop() : Result
}

