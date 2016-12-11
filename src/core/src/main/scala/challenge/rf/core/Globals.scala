package challenge.rf.core

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor, ThreadFactory}
import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


object Globals {

  /**
   * Configurable executionContext. To be honest scala is pretty lazy with its ec. Its an implicit val
   * which only allows a threadPool with nThreads = nCPU. If more threads needed use this ec.
   */
  implicit lazy val ec: ExecutionContextExecutor = {

    val nThreads = 8

    trait format {
      val count: AtomicInteger = new AtomicInteger(0)

      def formatThreadName[T <: Thread](f: String, t: T): T = {
        t.setName(String.format(f, count.getAndIncrement().asInstanceOf[AnyRef]))
        t
      }
    }

    class workflowThreadFactory(f: String) extends ThreadFactory with format {
      override def newThread(r: Runnable) = formatThreadName(f, new Thread(r))
    }

    ExecutionContext.fromExecutor(new ThreadPoolExecutor(nThreads, nThreads,
      0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable], new workflowThreadFactory("service-worker-pool-%d")))
  }
}
