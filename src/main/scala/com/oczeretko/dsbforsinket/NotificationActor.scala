package com.oczeretko.dsbforsinket

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.oczeretko.dsbforsinket.ImplicitConversions._
import com.oczeretko.dsbforsinket.Message.RegistrationTagsForTag
import com.oczeretko.dsbforsinket.fake.FakeNotificationHub
import com.windowsazure.messaging.{CollectionResult, INotificationHub, Notification, NotificationHub}

import scala.collection.JavaConversions._
import scala.concurrent.{Future, Promise}

class NotificationActor extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  val settings = Settings(context.system)

  lazy val default: INotificationHub =
    if (settings.useFakeNotificationHub)
      new FakeNotificationHub(settings.fakeNotificationHubBaseUrl)
    else
      new NotificationHub(settings.notificationHubConnectionString, settings.notificationHubName)

  def receive: Receive = {
    case msg: Message.Notify => {

      log.debug(s"Sending ${msg}.")
      val future = sendGcmPushNotification(msg.messageTag, msg.data)

      future onSuccess {
        case _ =>
          log.info(s"Notification sent")
          log.debug(s"Notification sent $msg")
      }

      future onFailure {
        case e => log.error(e, "Failed to send notification $msg")
      }
    }
    case msg@Message.GetRegistrationTagsForTag(timeTag) => {
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
    val registrationTagsFutures: List[Future[List[RegistrationTag]]] =
      for {
        bucketTag <- buckets
        p = Promise[CollectionResult]
        _ = default.getRegistrationsByTagAsync(bucketTag, p)

        bucketResult = for {
          collectionResult <- p.future
          tags = tagsFromCollection(collectionResult)
          registrations = tags.collect { case r: RegistrationTag => r }
          registrationsForTime = registrations.filter(_.time == timeTag.time)
          registrationsForTimeDistinct = registrationsForTime.distinct
        } yield registrationsForTimeDistinct

      } yield bucketResult

    // TODO: error handling
    val registrationTagsSuccessfulFutures: Future[List[List[RegistrationTag]]] =
      registrationTagsFutures.onlySuccessful

    val allFetchedTags = registrationTagsSuccessfulFutures.map(_.flatten)
    val allFetchedTagsDistinct = allFetchedTags.map(_.distinct)

    allFetchedTagsDistinct
  }

  private def tagsFromCollection(collectionResult: CollectionResult): List[NotificationTag] = {
    val tagsLists: List[List[String]] = collectionResult.getRegistrations.toList.map(_.getTags.toList)
    tagsLists.flatMap(_.map(NotificationTag.apiTagToRegistrationTag))
  }
}
