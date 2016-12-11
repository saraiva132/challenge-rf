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
    sv.stopAll()
    sv.start("service2")
    Thread.sleep(300)
    sv.stop("service2")
    Thread.sleep(500)
    assert(sv.activeServices().size == 0)
  }

  @Test
  def startStopServiceNoDependencies(): Unit = {
    sv.stopAll()
    Future { sv.start("service2") }
    Thread.sleep(100)
    Future { sv.stop("service2") }
    Thread.sleep(600)
    assert(sv.activeServices().size == 0)
  }

  @Test
  def startServiceDependencies(): Unit = {
    sv.stopAll()
    sv.start("service1") match {
      case OK => assert(false)
      case NOK => assert(true)
    }
  }

  @Test
  def stopServiceDependencies(): Unit = {
    sv.stopAll()
    sv.startWithDependencies("service1")
    Thread.sleep(1000)
    sv.stop("service2") match {
      case OK => assert(false)
      case NOK => assert(true)
    }
  }

  @Test
  def startMultipleServiceNoDependencies(): Unit = {
    sv.stopAll()
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
    sv.stopAll()
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
    sv.stopAll()
    sv.start("disabled") match {
      case OK => assert(false)
      case NOK => assert(true)
    }

    assert(sv.disabledServices().size == 1)
  }

  @Test
  def stressTestNumber1() :Unit = {
    sv.stopAll()
    Future { sv.startWithDependencies("service1") }
    Thread.sleep(500)
    sv.stopWithDependencies("service2")
    Thread.sleep(700)
    assert(sv.activeServices().size == 1)
  }
}


