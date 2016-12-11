package challenge.rf.it

import java.io.File

import challenge.rf.core.{ServiceManagerImpl, ServiceLoaderImpl}
import org.junit.Test

object systemTests {
  val pwd = new File(".").getCanonicalPath
  val sep = java.io.File.separator
  val resourcesDir = s"$pwd${sep}src${sep}it${sep}resources$sep"
  val source1 = scala.io.Source.fromFile(s"$resourcesDir${sep}db.json")
  val db = try source1.getLines.mkString("\n") finally source1.close()
  val serviceLoader = new ServiceLoaderImpl()
  val services = serviceLoader.loadAndValidate(db).get
  val sv = new ServiceManagerImpl(services)
}

import systemTests._

class systemTests {
  @Test
  def startServiceNoDependencies(): Unit = {
    sv.startAll()
    Thread.sleep(3000)
    sv.stopAll()
    Thread.sleep(3000)
    assert(sv.activeServices().size == 0)
  }
}
