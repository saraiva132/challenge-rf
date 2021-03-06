package challenge.rf.it

import java.io.File
import challenge.rf.core.{ServiceManagerImpl, ServiceLoaderImpl}
import org.junit._
import org.apache.log4j.xml.DOMConfigurator

object systemTests {
  val pwd = new File(".").getCanonicalPath
  val sep = java.io.File.separator
  val resourcesDir = s"$pwd${sep}src${sep}it${sep}resources$sep"
  val source1 = scala.io.Source.fromFile(s"$resourcesDir${sep}db.json")
  val db = try source1.getLines.mkString("\n") finally source1.close()
  val serviceLoader = new ServiceLoaderImpl()
  val services = serviceLoader.loadAndValidate(db).get
  val sv = new ServiceManagerImpl(services)
  val log4jConfig = resourcesDir + "/logging/log4j.xml"
  DOMConfigurator.configureAndWatch(log4jConfig,10)
}

import systemTests._

class systemTests {
  @Test
  def systemTest(): Unit = {
    sv.startAll()
    Thread.sleep(2500)
    sv.stopAll()
    Thread.sleep(2500)
    assert(sv.activeServices().size == 0)
    assert(sv.disabledServices().size == 1)

  }
}
