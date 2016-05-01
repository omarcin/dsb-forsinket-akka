package com.oczeretko.dsbforsinket

import akka.actor.{Actor, ActorLogging}
import scala.concurrent.ExecutionContext.Implicits.global

class PushNotificationActor extends Actor with ActorLogging {
  def receive: Receive = {
    case msg: Message.Notify => {

      log.info(s"Sending ${msg}.")
      val future = NotificationHubWrapper.sendGcmPushNotification(msg.messageTag, msg.data)

      future onSuccess {
        case _ => log.info(s"Notification sent $msg")
      }

      future onFailure {
        case e => log.error(e, "Failed to send notification $msg")
      }
    }
  }
}
