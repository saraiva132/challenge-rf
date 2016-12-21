package challenge.rf.it

import challenge.rf.api.{OK, Result, Service}
import org.slf4j.LoggerFactory

trait ServiceExample extends Service {
}

class ServiceExample1 extends ServiceExample {
  val logger = LoggerFactory.getLogger(classOf[ServiceExample1])

  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample1")
    logger.warn("Starting ServiceExample1")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample1")
    logger.warn("Stopping ServiceExample1")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample1")
    logger.warn("Running ServiceExample1")
  }
}

class ServiceExample2 extends ServiceExample {
  val logger = LoggerFactory.getLogger(classOf[ServiceExample2])
  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample2")
    logger.warn("Starting ServiceExample2")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample2")
    logger.warn("Stopping ServiceExample2")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample2")
    logger.warn("Running ServiceExample2")
  }
}

class ServiceExample3 extends ServiceExample {
  val logger = LoggerFactory.getLogger(classOf[ServiceExample3])

  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample3")
    logger.warn("Starting ServiceExample3")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample3")
    logger.warn("Stopping ServiceExample3")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample3")
    logger.warn("Running ServiceExample3")
  }
}

class ServiceExample4 extends ServiceExample {
  val logger = LoggerFactory.getLogger(classOf[ServiceExample4])

  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample4")
    logger.warn("Starting ServiceExample4")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample4")
    logger.warn("Stopping ServiceExample4")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample4")
    logger.warn("Running ServiceExample4")
  }
}

class ServiceExample5 extends ServiceExample {

  val logger = LoggerFactory.getLogger(classOf[ServiceExample5])

  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample5")
    logger.warn("Starting ServiceExample5")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample5")
    logger.warn("Stopping ServiceExample5")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample5")
    logger.warn("Running ServiceExample5")
  }
}

class ServiceExample6 extends ServiceExample {
  val logger = LoggerFactory.getLogger(classOf[ServiceExample6])

  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample6")
    logger.warn("Starting ServiceExample6")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample6")
    logger.warn("Stopping ServiceExample6")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample6")
    logger.warn("Running ServiceExample6")
  }
}