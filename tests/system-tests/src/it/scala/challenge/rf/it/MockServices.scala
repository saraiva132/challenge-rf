package challenge.rf.it

import challenge.rf.api.{OK, Result, Service}

class ServiceExample1 extends Service {
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

class ServiceExample2 extends Service {
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

class ServiceExample3 extends Service {
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

class ServiceExample4 extends Service {
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