package com.oczeretko.dsbforsinket

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.oczeretko.dsbforsinket.ImplicitConversions._
import com.oczeretko.dsbforsinket.Message.RegistrationTagsForTag
import com.typesafe.config.ConfigFactory
import com.windowsazure.messaging.{CollectionResult, Notification, NotificationHub}

import scala.collection.JavaConversions._
import scala.concurrent.{Future, Promise}

class NotificationActor extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher

  lazy val default: NotificationHub = {
    val config = ConfigFactory.load("secrets")
    val connectionString = config.getString("secrets.notificationHub.connectionString")
    val hubName = config.getString("secrets.notificationHub.name")
    new NotificationHub(connectionString, hubName)
  }

  def receive: Receive = {
    case msg: Message.Notify => {

      log.debug(s"Sending ${msg}.")
      val future = sendGcmPushNotification(msg.messageTag, msg.data)

      future onSuccess {
        case _ => log.info(s"Notification sent $msg")
      }

      future onFailure {
        case e => log.error(e, "Failed to send notification $msg")
      }
    }
    case msg @ Message.GetRegistrationTagsForTag(timeTag) => {
      log.debug(s"$msg.")
      val originalSender = sender
      val response = getRegistrationsTagsByTimeTagAsync(timeTag).map(RegistrationTagsForTag(timeTag, _))
      response pipeTo originalSender
    }
  }

  def sendGcmPushNotification(tag: String, data: Map[String, String]): Future[Unit] = {
    val dataRows = data.map { case (k, v) => "\"" + k + "\": \"" + v + "\"" }
    val dataJson = dataRows.mkString(",")
    val body = "{ \"priority\" : \"high\", \"time_to_live\" : 0, \"data\": { " + dataJson + " } }"
    val notification = Notification.createGcmNotifiation(body)
    val p = Promise[AnyRef]
    default.sendNotificationAsync(notification, tag, p)
    p.future.map(dummy => ())
  }

  def getRegistrationsTagsByTimeTagAsync(timeTag: TimeTag): Future[List[RegistrationTag]] = {
    import FutureEx._

    val buckets: List[String] = Tags.bucketsFromTag(timeTag.messageTag)
    val futureResults: List[Future[List[NotificationTag]]] =
      buckets.map(bucketTag => {
        val p = Promise[CollectionResult]
        default.getRegistrationsByTagAsync(bucketTag, p)
        p.future.map(tagsFromCollection)
      })

    val registrationTagsFutures : List[Future[List[RegistrationTag]]] =
      futureResults.map(_.map(_.collect { case r: RegistrationTag => r }))

    val registrationTagsSuccessfulFutures: Future[List[List[RegistrationTag]]] =
      registrationTagsFutures.onlySuccessful

    val allFetchedTags = registrationTagsSuccessfulFutures.map(_.flatten)
    val tagsForTime = allFetchedTags.map(_.filter(_.time == timeTag.time))

    tagsForTime
  }

  private def tagsFromCollection(collectionResult: CollectionResult): List[NotificationTag] = {
    val tagsLists: List[List[String]] = collectionResult.getRegistrations.toList.map(_.getTags.toList)
    tagsLists.flatMap(_.map(NotificationTag.apiTagToRegistrationTag))
  }
}
