package com.oczeretko.dsbforsinket

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object FutureEx {
  implicit def toFutureEx[T](futures: List[Future[T]])(implicit ec: ExecutionContext): FutureEx[T] =
    FutureEx(futures)(ec)
}

case class FutureEx[T](futures: List[Future[T]])(implicit ec: ExecutionContext) {
  def onlySuccessful: Future[List[T]] = {
    val futureTries: List[Future[Try[T]]] =
      futures.map(f => f.map(Success(_)).recover { case e => Failure(e) })
    Future.sequence(futureTries).map(_.collect { case Success(ts) => ts })
  }
}
