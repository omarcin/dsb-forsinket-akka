package com.oczeretko.dsbforsinket

import com.windowsazure.messaging.{CollectionResult, NotificationHub, Registration}

import scala.concurrent.{Future, Promise}
import ImplicitConversions._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

object NotificationHubWrapper {

  lazy val default: NotificationHub = {
    val config = ConfigFactory.load("secrets")
    val connectionString = config.getString("secrets.notificationHub.connectionString")
    val hubName = config.getString("secrets.notificationHub.name")
    new NotificationHub(connectionString, hubName)
  }

  def getRegistrationsByTagAsync(tag: String): Future[List[Registration]] = {
    val p = Promise[CollectionResult]
    default.getRegistrationsByTagAsync(tag, p)
    p.future.map(_.getRegistrations.toList)
  }
}

