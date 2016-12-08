package challenge.rf.core

import challenge.rf.api.{ServiceMetadata, ServiceLoader}
import challenge.rf.api.{Result, OK, NOK}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.annotation.tailrec

class ServiceLoaderImpl extends ServiceLoader {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def load(file: String): Vector[ServiceMetadata] =
    mapper.readValue(file, classOf[Vector[ServiceMetadata]])

  def loadAndValidate(file: String): Option[Vector[ServiceMetadata]] = {
    val metadata = mapper.readValue(file, classOf[Vector[ServiceMetadata]])
    validate(metadata) match {
      case OK => Some(metadata)
      case NOK => None
    }
  }

  def validate(metadata: Vector[ServiceMetadata]): Result = {

    val detectDuplicates = metadata.groupBy(_.name).exists(_._2.size > 1)

    def detectGraphLoop(sv: ServiceMetadata): Boolean = {
      @tailrec
      def loop(deps: Vector[ServiceMetadata]): Boolean = {
        if(deps.size == 0) false
        else if(deps.exists(_.name equals sv)) true
        else if(deps.exists(_.dependencies.exists(_ equals sv))) true
        else loop(deps.flatMap(it => metadata.find(_.name equals it)))
      }
      loop(sv.dependencies.flatMap(it => metadata.find(_.name equals it)))
    }

    if(!detectDuplicates && !metadata.forall(detectGraphLoop)) OK
    else NOK
  }
}
