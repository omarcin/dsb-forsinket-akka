package com.oczeretko.dsbforsinket

import org.apache.http.concurrent.FutureCallback

import scala.concurrent.Promise

object ImplicitConversions {
  implicit def toFutureCallback[T](f: Promise[T]): FutureCallback[T] = MyCallback(f)

  private case class MyCallback[T](f: Promise[T]) extends FutureCallback[T] {

    override def cancelled(): Unit = {
      f.failure(new Exception("Canceled"))
    }

    override def completed(t: T): Unit = {
      f.success(t)
    }

    override def failed(e: Exception): Unit = {
      f.failure(e)
    }
  }

}
