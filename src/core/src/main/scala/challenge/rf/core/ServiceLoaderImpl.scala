package challenge.rf.core

import challenge.rf.api.{ServiceMetadata, ServiceLoader}
import challenge.rf.api.{Result, OK, NOK}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Try

class ServiceLoaderImpl extends ServiceLoader {

  implicit private val formats = DefaultFormats

  /**
   * Load config file into ORM. Validations are assumed to be done by the caller.
   *
   * @param config - Configuration file in String format. (Assumed UTF-8)
   * @return - The list of ServiceMetadata
   */
  def load(config: String): Vector[ServiceMetadata] = parse(config).extract[Vector[ServiceMetadata]]

  /**
   * Load and validate config file into ORM.
   *
   * @param config - Configuration file in String format. (Assumed UTF-8)
   * @return - The list of ServiceMetadata
   */
  def loadAndValidate(config: String): Option[Vector[ServiceMetadata]] = Try{
    val metadata = parse(config).extract[Vector[ServiceMetadata]]
    validate(metadata) match {
      case OK => Some(metadata)
      case NOK => None
    }
  }.getOrElse(None)


  /**
   * Validation method. Sorry, the graph loop detection is not tail recursive. =( And the algorithm is kinda dumb..
   *
   * @param metadata - metadata to be validated
   * @return - OK if valid.
   *           NOK if not valid.
   */
  def validate(metadata: Vector[ServiceMetadata]): Result = {

    /* Duplicated names*/
    val detectDuplicates = metadata.size == metadata.map(_.name).distinct.size

    /* Invalid dependencies*/
    val detectInvalidDependencies = metadata.flatMap(_.dependencies).
      distinct.forall( it => metadata.exists( _.name equals it))

    /* Cyclic dependencies */
    def detectGraphLoop(sv: ServiceMetadata) = {

      def nameToService(name : String) : Option[ServiceMetadata] = metadata.find(_.name equals name)

      def loop(deps: Vector[ServiceMetadata], visited : Vector[String]): Boolean = {
        if(deps.size == 0) false /* All good*/
        else if(deps.exists(_.dependencies.exists( _ equals sv.name))) true /* Cyclic Dependency*/
        else if(visited.exists( it => deps.exists(_.name equals it))) true /* Transitive Dependency*/
        else deps.exists(s => loop(s.dependencies.flatMap(nameToService), visited :+ s.name)) /* Recursively call all dependencies*/
      }

      loop(sv.dependencies.flatMap(nameToService), Vector.empty)
    }

    if(!detectDuplicates || !detectInvalidDependencies || metadata.exists(detectGraphLoop)) NOK
    else OK
  }
}