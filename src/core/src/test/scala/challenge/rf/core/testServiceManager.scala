package challenge.rf.core

import java.io.File

import challenge.rf.api.{NOK, OK}
import org.junit.Test
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object testServiceManager {
  val pwd = new File(".").getCanonicalPath
  val sep = java.io.File.separator
  val resourcesDir = s"$pwd${sep}src${sep}test${sep}resources$sep"
  val source1 = scala.io.Source.fromFile(s"$resourcesDir${sep}db.json")
  val db = try source1.getLines.mkString("\n") finally source1.close()
  val serviceLoader = new ServiceLoaderImpl()
  val services = serviceLoader.loadAndValidate(db).get
  val sv = new ServiceManagerImpl(services)
}

class testServiceManager {

  import testServiceManager._

  @Test
  def startServiceNoDependencies(): Unit = {
    sv.start("service2")
    Thread.sleep(300)
    sv.stop("service2")
    Thread.sleep(500)
    assert(sv.activeServices().size == 0)
  }

  @Test
  def startStopServiceNoDependencies(): Unit = {
    Future { sv.start("service2") }
    Thread.sleep(100)
    Future { sv.stop("service2") }
    Thread.sleep(600)
    assert(sv.activeServices().size == 0)
  }

  @Test
  def startServiceDependencies(): Unit = {
    sv.start("service1") match {
      case OK => assert(false)
      case NOK => assert(true)
    }
  }

  @Test
  def startMultipleServiceNoDependencies(): Unit = {
    sv.start("service2")
    sv.start("service3")
    sv.start("service4")
    Thread.sleep(300)
    sv.stop("service2")
    sv.stop("service3")
    sv.stop("service4")
    Thread.sleep(500)
    assert(sv.activeServices().size == 0)
  }

  @Test
  def startServiceWithDependencies(): Unit = {
    sv.startWithDependencies("service1")
    Thread.sleep(1000)
    sv.stopWithDependencies("service2")
    Thread.sleep(500)
    sv.stopAll()
    Thread.sleep(500)
    assert(sv.activeServices().size == 0)
  }

  @Test
  def disabledServiceDependencies(): Unit = {
    sv.start("disabled") match {
      case OK => assert(false)
      case NOK => assert(true)
    }

    assert(sv.disabledServices().size == 1)
  }


}


