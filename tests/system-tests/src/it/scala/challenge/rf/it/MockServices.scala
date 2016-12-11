package challenge.rf.it

import challenge.rf.api.{OK, Result, Service}
import org.slf4j.LoggerFactory

trait ServiceExample extends Service {
  val logger = LoggerFactory.getLogger(classOf[ServiceExample])
}

class ServiceExample1 extends ServiceExample {

  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample1")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample1")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample1")
  }
}

class ServiceExample2 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample2")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample2")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample2")
  }
}

class ServiceExample3 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample3")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample3")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample3")
  }
}

class ServiceExample4 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample4")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample4")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample4")
  }
}

class ServiceExample5 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample5")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample5")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample5")
  }
}

class ServiceExample6 extends ServiceExample {
  override def start(): Result = {
    Thread.sleep(200)
    println("Starting ServiceExample6")
    OK
  }

  override def stop(): Result = {
    Thread.sleep(200)
    println("Stopping ServiceExample6")
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample6")
  }
}