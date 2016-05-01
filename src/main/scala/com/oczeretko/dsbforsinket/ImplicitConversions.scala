package com.oczeretko.dsbforsinket

import org.apache.http.concurrent.FutureCallback
import retrofit2.{Call, Callback, Response}

import scala.concurrent.Promise

object ImplicitConversions {

  implicit def toCallback[T](f: Promise[T]) : Callback[T] = PromiseResponseCallback(f)

  implicit def toFutureCallback[T](f: Promise[T]): FutureCallback[T] = PromiseFutureCallback(f)

  private case class PromiseResponseCallback[T](f : Promise[T]) extends Callback[T] {
    override def onFailure(call: Call[T], t: Throwable): Unit = f.failure(t)

    override def onResponse(call: Call[T], response: Response[T]): Unit =
      if (response.isSuccessful)
        f.success(response.body())
      else
        f.failure(new Exception(response.errorBody().string()))
  }

  private case class PromiseFutureCallback[T](f: Promise[T]) extends FutureCallback[T] {
    override def cancelled(): Unit = f.failure(new Exception("Canceled"))
    override def completed(t: T): Unit = f.success(t)
    override def failed(e: Exception): Unit = f.failure(e)
  }
}
