package challenge.rf.it

import challenge.rf.api.{OK, Result, Service}
import org.apache.log4j.Logger

trait ServiceExample extends Service {
  val logger = Logger.getLogger(classOf[ServiceExample])
}

class ServiceExample1 extends ServiceExample {

  override def start(): Result = {
    Thread.sleep(200)
    logger.debug("Starting ServiceExample1")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    logger.debug("Stopping ServiceExample1")
    OK
  }

  override def run(): Unit = {
    logger.debug("Running ServiceExample1")
  }
}

class ServiceExample2 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    logger.debug("Starting ServiceExample2")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    logger.debug("Stopping ServiceExample2")
    OK
  }

  override def run(): Unit = {
    logger.debug("Running ServiceExample2")
  }
}

class ServiceExample3 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    logger.debug("Starting ServiceExample3")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    logger.debug("Stopping ServiceExample3")
    OK
  }

  override def run(): Unit = {
    logger.debug("Running ServiceExample3")
  }
}

class ServiceExample4 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    logger.debug("Starting ServiceExample4")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    logger.debug("Stopping ServiceExample4")
    OK
  }

  override def run(): Unit = {
    logger.debug("Running ServiceExample4")
  }
}