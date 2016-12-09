package challenge.rf.core

import java.io.File

import challenge.rf.api.{OK, NOK}
import org.junit.Test

object testServiceLoader {
  val pwd = new File(".").getCanonicalPath
  val sep = java.io.File.separator
  val resourcesDir = s"$pwd${sep}src${sep}test${sep}resources$sep"
  val source1 = scala.io.Source.fromFile(s"$resourcesDir${sep}db.json")
  val source2 = scala.io.Source.fromFile(s"$resourcesDir${sep}fakedb.json")
  val source3 = scala.io.Source.fromFile(s"$resourcesDir${sep}loopdb.json")
  val source4 = scala.io.Source.fromFile(s"$resourcesDir${sep}invalidDeps.json")
  val source5 = scala.io.Source.fromFile(s"$resourcesDir${sep}loop1db.json")
  val source6 = scala.io.Source.fromFile(s"$resourcesDir${sep}loop2db.json")

  val dbGood = try source1.getLines.mkString("\n") finally source1.close()
  val dbFake = try source2.getLines.mkString("\n") finally source2.close()
  val dbLoop = try source3.getLines.mkString("\n") finally source3.close()
  val invalidDeps = try source4.getLines.mkString("\n") finally source4.close()
  val dbLoop1 = try source5.getLines.mkString("\n") finally source5.close()
  val dbLoop2 = try source6.getLines.mkString("\n") finally source6.close()

  val serviceLoader = new ServiceLoaderImpl()
}


class testServiceLoader {

  import testServiceLoader._

  @Test
  def load(): Unit = {
    val services = serviceLoader.load(dbGood)
    assert(services.size > 0)
  }

  @Test
  def loadAndValidate(): Unit = {
    serviceLoader.loadAndValidate(dbGood) match {
      case Some(_) => assert(true)
      case None => assert(false)
    }
  }

  @Test
  def loadAndValidateAndFailDuplicated(): Unit = {
    serviceLoader.loadAndValidate(dbFake) match {
      case Some(_) => assert(false)
      case None => assert(true)
    }
  }

  @Test
  def loadAndValidateAndFailCycle(): Unit = {
    serviceLoader.loadAndValidate(dbLoop) match {
      case Some(_) => assert(false)
      case None => assert(true)
    }
  }

  @Test
  def loadAndValidateInvalidDependencies(): Unit = {
    serviceLoader.loadAndValidate(invalidDeps) match {
      case Some(_) => assert(false)
      case None => assert(true)
    }
  }

  @Test
  def loadAndValidateAndFailCycle1(): Unit = {
    serviceLoader.loadAndValidate(dbLoop1) match {
      case Some(_) => assert(false)
      case None => assert(true)
    }
  }

  @Test
  def loadAndValidateAndFailCycle2(): Unit = {
    serviceLoader.loadAndValidate(dbLoop2) match {
      case Some(_) => assert(false)
      case None => assert(true)
    }
  }
}
