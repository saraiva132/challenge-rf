package challenge.rf.api

/**
 * Service Manager API
 */
trait ServiceManager {

  /**
   * Start a service. Blocking method.
   *
   * @param name -> Name of the service
   * @param withDeps : True -> Normal procedure.
   *                   False -> Aborts if any dependency is not Running.
   * @return - Operation Result: OK/NOK
   */
  def start(name : String, withDeps : Boolean) : Result

  /**
   * Start a service and start all dependencies if needed. Blocking method.
   *
   * This service will stop all dependencies and transitive dependencies that are not running or stopping.
   *
   * @param name -> Name of the service
   * @return - Operation Result: OK/NOK
   */
  def startWithDependencies(name : String) : Result

  /**
   * Stop a service. Blocking method.
   *
   * @param name -> Name of the service
   * @param withDeps : True -> Normal procedure.
   *                   False -> Aborts if any dependant service is Running or Starting.
   * @return - Operation Result: OK/NOK
   */
  def stop(name : String, withDeps : Boolean)  : Result

  /**
   * Stop a service and stop all dependant services if needed. Blocking method.
   *
   * This service will stop all dependant services and transitive dependant services
   * that are running or starting.
   *
   * @param name -> Name of the service
   * @return - Operation Result: OK/NOK
   */
  def stopWithDependencies(name : String) : Result

  /**
   * Start all services. (Taking in account dependencies order)
   *
   * Non-blocking method.
   */
  def startAll()

  /**
   * Stop all services. (Taking in account dependant services order)
   *
   * Non-blocking method.
   */
  def stopAll()
}

/**
 * Service API
 *
 * Service extends runnable so the running logic should be implemented in the method run()
 *
 */
trait Service extends Runnable{

  /**
   * Start method indicates the service should do all preparations to run. This means loading all configs
   * and to any setup necessary to run.
   *
   * @return - Operation Result: OK/NOK
   */
  def start() : Result

  /**
   * Stop method indicates the service should do all preparations to stop. This means shutting down all current
   * operations and for instance persist any cached information. The service should also end the run() method.
   *
   * @return - Operation Result: OK/NOK
   */
  def stop() : Result
}

/**
 * Service Metadata. Will be parsed from a json document.
 *
 * @param name - Service name must be unique
 * @param cls - Absolute path to the class to be loaded in runtime. The class must extend Service.
 * @param dependencies - List of dependencies for this service.
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
 * Loads and validates service metadata
 */
trait ServiceLoader {

  /**
   * Load the service metadata from a file (string with file content) and parse it to object.
   *
   * @param file - The configuration
   * @return - Set of service metadata
   */
  def load(file : String) : Vector[ServiceMetadata]

  /**
   * Validate if the config is valid:
   *  -> No cyclic dependencies.
   *  -> No duplicated services.
   *  -> No invalid dependencies (pointing to a non-existent service)
   *
   * @return - Operation Result: OK/NOK
   */
  def validate(metadata :Vector[ServiceMetadata]) : Result

  /**
   * Loads the file and also validates it.
   *
   * @param file - config
   * @return - Some(services) if valid else None
   */
  def loadAndValidate(file : String) : Option[Vector[ServiceMetadata]]
}