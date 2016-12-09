package challenge.rf.core

import java.io.File

import challenge.rf.api.ServiceMetadata


object playground extends App {

  val pwd = new File(".").getCanonicalPath
  val sep = java.io.File.separator
  val resourcesDir = s"$pwd${sep}src${sep}test${sep}resources$sep"
  val source1 = scala.io.Source.fromFile(s"$resourcesDir${sep}db.json")
  val source2 = scala.io.Source.fromFile(s"$resourcesDir${sep}fakedb.json")
  val source3 = scala.io.Source.fromFile(s"$resourcesDir${sep}loopdb.json")

  val dbGood = try source1.getLines.mkString("\n") finally source1.close()
  val dbFake = try source2.getLines.mkString("\n") finally source2.close()
  val dbLoop = try source3.getLines.mkString("\n") finally source3.close()

  val serviceLoader = new ServiceLoaderImpl()

  val services = serviceLoader.load(dbGood)

  val wtfman = List.empty[ServiceMetadata]

  services.map(_.name).foreach(println)

  wtfman.foreach(println)

  wtfman.map(_.name).foreach(println)

}
