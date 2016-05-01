package com.oczeretko.dsbforsinket

import com.windowsazure.messaging.{CollectionResult, Notification, NotificationHub, Registration}

import scala.concurrent.{ExecutionContext, Future, Promise}
import ImplicitConversions._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._

object NotificationHubWrapper {

  lazy val default: NotificationHub = {
    val config = ConfigFactory.load("secrets")
    val connectionString = config.getString("secrets.notificationHub.connectionString")
    val hubName = config.getString("secrets.notificationHub.name")
    new NotificationHub(connectionString, hubName)
  }

  def getRegistrationsByTagAsync(tag: String)(implicit context: ExecutionContext): Future[List[Registration]] = {
    val p = Promise[CollectionResult]
    default.getRegistrationsByTagAsync(tag, p)
    p.future.map(_.getRegistrations.toList)
  }

  def sendGcmPushNotification(tag: String, data: Map[String, String])(implicit context: ExecutionContext): Future[Unit] = {
    val dataJson = data.foldLeft("") { case (acc, (k, v)) => acc + "\"" + k + "\": \"" + v + "\"" }
    val body = "{ \"priority\" : \"high\", \"time_to_live\" : 0, \"data\": { " + dataJson + " } }"
    val notification = Notification.createGcmNotifiation(body)
    val p = Promise[AnyRef]
    default.sendNotificationAsync(notification, tag, p)
    p.future.map(dummy => ())
  }
}

