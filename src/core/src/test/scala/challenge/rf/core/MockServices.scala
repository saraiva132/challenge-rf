package challenge.rf.core

import challenge.rf.api.{OK, Result, Service}

class ServiceExample1 extends Service {
  override def start(): Result = {
    println("Starting ServiceExample1")
    Thread.sleep(2000)
    OK
  }

  override def stop(): Result = {
    println("Stopping ServiceExample1")
    Thread.sleep(2000)
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample1")
  }
}

class ServiceExample2 extends Service {
  override def start(): Result = {
    println("Starting ServiceExample2")
    Thread.sleep(2000)
    OK
  }

  override def stop(): Result = {
    println("Stopping ServiceExample2")
    Thread.sleep(2000)
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample2")
  }
}

class ServiceExample3 extends Service {
  override def start(): Result = {
    println("Starting ServiceExample3")
    Thread.sleep(2000)
    OK
  }

  override def stop(): Result = {
    println("Stopping ServiceExample3")
    Thread.sleep(2000)
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample3")
  }
}

class ServiceExample4 extends Service {
  override def start(): Result = {
    println("Starting ServiceExample4")
    Thread.sleep(2000)
    OK
  }

  override def stop(): Result = {
    println("Stopping ServiceExample4")
    Thread.sleep(2000)
    OK
  }

  override def run(): Unit = {
    println("Running ServiceExample4")
  }
}