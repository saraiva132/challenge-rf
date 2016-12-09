package challenge.rf.core

import java.io.File

import challenge.rf.api.{NOK, OK, Result, Service}
import org.junit.Test


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
    Thread.sleep(3000)
    sv.stop("service2")
    Thread.sleep(5000)
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
    Thread.sleep(3000)
    sv.stop("service2")
    sv.stop("service3")
    sv.stop("service4")
    Thread.sleep(5000)
  }

}

class ServiceExample extends Service {
  override def start(): Result = {
    println("Starting ServiceExample")
    Thread.sleep(2000)
    OK
  }

  override def stop(): Result = {
    println("Stopping ServiceExample")
    Thread.sleep(2000)
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample")
  }
}
