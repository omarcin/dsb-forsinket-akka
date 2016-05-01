package com.oczeretko.dsbforsinket

import java.time.{DayOfWeek, LocalDateTime, ZoneId}

import akka.actor.{Actor, ActorLogging, Props}
import com.oczeretko.dsbforsinket.Message.CheckForDelay

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DeparturesManagerActor extends Actor with ActorLogging {

  def shouldRun(dateTime: LocalDateTime): Boolean =
    // TODO: temporary
    // dateTime.getDayOfWeek != DayOfWeek.SATURDAY && dateTime.getDayOfWeek != DayOfWeek.SUNDAY
  true

  def receive: Receive = {
    case Message.FindSubscribers => {
      log.info("Received message: FindSubscribers")
      val cphTime = LocalDateTime.now(ZoneId.of("Europe/Copenhagen"))

      if (shouldRun(cphTime)) {


        val minutesRounded = (cphTime.getMinute / 15) * 15
        val timeTag = f"${Tags.timeTagPrefix}%s${cphTime.getHour}%s:$minutesRounded%02d"
        val timeTagNoPrefix = f"${cphTime.getHour}%s:$minutesRounded%02d"

        log.info(s"$timeTag : FindSubscribers")
        val stationsFuture = stationsForTimeTag(timeTag)

        stationsFuture onSuccess {
          case Seq() => {
            log.info(s"$timeTag No subscriptions")
          }
          case stations => {

            val checker = context.actorOf(Props[DeparturesCheckActor])

            for {
              station <- stations
              messageTag = s"$station-$timeTagNoPrefix"
              msg = CheckForDelay(messageTag, station, timeTagNoPrefix, isTest = false)
            } yield {
              log.info(s"$timeTag subscription for $station")
              checker ! msg
            }
          }
        }

        stationsFuture onFailure {
          case error => {
            log.error(error, s"$timeTag Failed to schedule work")
          }
        }
      } else {
        log.info("Skipping because shouldRun returned false")
      }
    }
  }

  def stationsForTimeTag(timeTag: String): Future[Seq[String]] = {
    import scala.collection.JavaConversions._

    val buckets = Tags.bucketsFromTag(timeTag)
    val registrationsFutures = buckets.map(NotificationHubWrapper.getRegistrationsByTagAsync)
    val registrationListsFuture = collectResults(registrationsFutures)

    registrationListsFuture.map(seq => {
      val registrations = seq.flatten
      val tags = registrations.flatMap(r => r.getTags.toIterable)
      val tagsStationDistinct = tags.filter(_.startsWith(Tags.stationTagPrefix)).distinct
      val tagsNoPrefix = tagsStationDistinct.map(_.substring(Tags.stationTagPrefix.length))
      tagsNoPrefix
    })
  }

  def collectResults[T](futures: Seq[Future[T]]): Future[Seq[T]] = {
    val noFails = futures.map(f => f.map(Some(_)).recover({ case e => None }))
    Future.sequence(noFails).map(seq => seq.flatten)
  }
}

