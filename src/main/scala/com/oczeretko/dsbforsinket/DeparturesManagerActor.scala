package com.oczeretko.dsbforsinket

import java.time.{DayOfWeek, LocalDateTime, ZoneId}

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

class DeparturesManagerActor extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val timeout: Timeout = 60 seconds
  val departuresActor = context.actorOf(Props[DeparturesCheckActor])
  val notificationActor = context.actorOf(Props[NotificationActor])

  def shouldRun(dateTime: LocalDateTime): Boolean =
    dateTime.getDayOfWeek != DayOfWeek.SATURDAY && dateTime.getDayOfWeek != DayOfWeek.SUNDAY

  def receive: Receive = {
    case Message.FindSubscribers => {
      log.info("Received message: FindSubscribers")
      val cphTime = LocalDateTime.now(ZoneId.of("Europe/Copenhagen"))

      if (shouldRun(cphTime)) {

        val minutesRounded = (cphTime.getMinute / 15) * 15
        val timeTag = f"${Tags.timeTagPrefix}%s${cphTime.getHour}%02d:$minutesRounded%02d"
        val timeTagNoPrefix = f"${cphTime.getHour}%s:$minutesRounded%02d"

        log.info(s"$timeTag : FindSubscribers")
        val response = (notificationActor ? Message.GetRegistrationTagsForTag(timeTag)).mapTo[Message.RegistrationTagsForTag]

        response onSuccess {
          case Message.RegistrationTagsForTag(_, Seq()) => {
            log.info(s"$timeTag No subscriptions")
          }
          case Message.RegistrationTagsForTag(timeTagNoPrefix, registrations) => {

            for {
              registration <- registrations
              msg = Message.CheckForDelay(registration, isTest = false)
            } yield {
              log.info(s"$timeTag subscription for $registration")
              departuresActor ! msg
            }
          }
        }

        response onFailure {
          case error => {
            log.error(error, s"$timeTag Failed to get registrations")
          }
        }
      } else {
        log.info("Skipping because shouldRun returned false")
      }
    }
  }
}

