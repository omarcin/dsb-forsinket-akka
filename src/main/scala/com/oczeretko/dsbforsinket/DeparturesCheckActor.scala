package com.oczeretko.dsbforsinket

import java.util.NoSuchElementException

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.language.postfixOps

class DeparturesCheckActor extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  val pushNotificationActor: ActorRef = context.actorOf(Props[NotificationActor], "notification")

  def receive = {
    case msg: Message.CheckForDelay => {
      log.debug(s"Received $msg")

      val delayedDeparturesFuture = RejseplannenApi.getDelayedDepartures(msg.tag.station);
      val messageFuture =
        for {
          delayedDepartures <- delayedDeparturesFuture
          _ = log.info(s"Delayed departures: ${msg.tag} ${delayedDepartures.length}.")
          _ = for {
            unrecognized <- delayedDepartures.filter(_.isInstanceOf[GenericDeparture])
          } log.info(s"Could not match departure $unrecognized")

          if delayedDepartures.nonEmpty
          _ = log.debug(s"Preparing push message to tag: ${msg.tag.messageTag}.")

          mapWithCount = Map[String, String]("delayedCount" -> delayedDepartures.length.toString)
          firstFive = delayedDepartures.take(5)
          data = mapWithCount ++ toNotificationData(firstFive)
        } yield Message.Notify(msg.tag.messageTag, data)

      messageFuture onSuccess {
        case message => pushNotificationActor ! message
      }

      messageFuture onFailure {
        case e: NoSuchElementException => log.info("No delayed departures")
        case e => log.error(e, "Failed to get departure data")
      }
    }
  }

  private def toNotificationData(departures: Iterable[Departure]) = {
    val dataMap = Map[String, String]()
    departures.foldLeft((0, dataMap)) {
      case ((index, map), departure) => {

        val updatedMap = map ++ Map[String, String](
          s"departureName$index" -> departure.displayName,
          s"departureTime$index" -> departure.time,
          s"departureNewTime$index" -> departure.updatedTime.getOrElse(""),
          s"departureCancelled$index" -> departure.isCancelled.toString
        )

        (index + 1, updatedMap)
      }
    }._2
  }
}
