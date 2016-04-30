package com.oczeretko.dsbforsinket

import com.windowsazure.messaging.{CollectionResult, NotificationHub, Registration}

import scala.concurrent.{Future, Promise}
import ImplicitConversions._
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

object NotificationHubWrapper {

  def default: NotificationHub = {
    new NotificationHub("", "")
  }

  def getRegistrationsByTagAsync(tag: String): Future[List[Registration]] = {
    val p = Promise[CollectionResult]
    default.getRegistrationsByTagAsync(tag, p)
    p.future.map(_.getRegistrations.toList)
  }
}

