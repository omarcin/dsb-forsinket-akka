package com.oczeretko.dsbforsinket

import java.util.NoSuchElementException

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.ExecutionContext.Implicits.global

class DeparturesCheckActor extends Actor with ActorLogging {

  def receive = {
    case msg: Message.CheckForDelay => {
      log.info(s"Received $msg")

      val delayedDeparturesFuture = RejseplannenApi.getDelayedDepartures(msg.station);
      val dataFuture =
        for {
          delayedDepartures <- delayedDeparturesFuture
          _ = log.info(s"Delayed departures: ${delayedDepartures.length}.")
          if delayedDepartures.nonEmpty
          _ = log.info(s"Preparing push message to tag: ${msg.messageTag}.")
          mapWithCount = Map[String, String]("delayedCount" -> delayedDepartures.length.toString)
          firstFive = delayedDepartures.take(5)
          data = firstFive.foldLeft((0, mapWithCount)) {
            case ((index, map), departure) => {
              val updatedMap = map ++ Map[String, String](
                s"departureName$index" -> s"${departure.getName} <i>${departure.getDirection}</i>",
                s"departureTime$index" -> departure.getTime,
                s"departureDelay$index" -> departure.getUpdatedTime
              )
              (index, updatedMap)
            }
          }
        } yield data._2

      dataFuture onSuccess {
        case data => context.actorOf(Props[PushNotificationActor]) ! Message.Notify(msg.messageTag, data)
      }

      dataFuture onFailure {
        case e : NoSuchElementException => log.info("No delayed departures")
        case e => log.error(e, "Failed to get departure data")
      }
    }
  }
}





