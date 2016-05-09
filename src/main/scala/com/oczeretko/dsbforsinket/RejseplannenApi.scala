package com.oczeretko.dsbforsinket

import com.oczeretko.dsbforsinket.ImplicitConversions._
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future, Promise}

object RejseplannenApi {

  private val BASE_URL = "http://xmlopen.rejseplanen.dk/bin/rest.exe/"

  lazy val retrofit: Retrofit =
    new Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  lazy val service: RejseplanenService = retrofit.create(classOf[RejseplanenService]);

  def getDelayedDepartures(station: String)(implicit context: ExecutionContext): Future[List[Departure]] = {
    val promise = Promise[RejseplanenService.DepartureResult]
    service.getDepartures(station).enqueue(promise)
    for {
      result <- promise.future
      departures = result.getDepartureBoard.getDepartures.toList.map(toDeparture)
      delayedDepartures = departures.filter(d => d.isDelayed || d.isCancelled)
    } yield delayedDepartures
  }

  private def toDeparture(di: RejseplanenService.Departure): Departure =
    di.getType match {
      case "S" => STrainDeparture(di.getName, di.getTime, di.getDate, di.getMessages, di.getTrack, Option(di.getUpdatedTime), Option(di.getUpdatedDate), Option(di.getUpdatedTrack), di.getFinalStop, di.getDirection, di.isCancelled, Option(di.getState))
      case "TOG" | "REG" => RegionalTrainDeparture(di.getName, di.getTime, di.getDate, di.getMessages, di.getTrack, Option(di.getUpdatedTime), Option(di.getUpdatedDate), Option(di.getUpdatedTrack), di.getFinalStop, di.getDirection, di.isCancelled, Option(di.getState))
      case "M" => MetroDeparture(di.getName, di.getTime, di.getDate, di.getMessages, di.getTrack, Option(di.getUpdatedTime), Option(di.getUpdatedDate), Option(di.getUpdatedTrack), di.getFinalStop, di.getDirection, di.isCancelled, Option(di.getState))
      case "BUS" | "EXB" => BusDeparture(di.getName, di.getTime, di.getDate, di.getMessages, Option(di.getUpdatedTime), Option(di.getUpdatedDate), di.getFinalStop, di.getDirection, di.isCancelled, Option(di.getState))
      case _ => GenericDeparture(di.getType, di.getName, di.getTime, di.getDate, di.getMessages, Option(di.getUpdatedTime), Option(di.getUpdatedDate), di.getFinalStop, di.getDirection, di.isCancelled, Option(di.getState))
    }

}

sealed trait Departure {
  def name: String

  def time: String

  def date: String

  def messages: String

  def updatedTime: Option[String]

  def updatedDate: Option[String]

  def finalStop: String

  def direction: String

  def isDelayed: Boolean = updatedTime.isDefined

  def isCancelled: Boolean

  def state: Option[String]

  def displayName: String = direction
}

case class RegionalTrainDeparture(name: String,
                                  time: String,
                                  date: String,
                                  messages: String,
                                  track: String,
                                  updatedTime: Option[String],
                                  updatedDate: Option[String],
                                  updatedTrack: Option[String],
                                  finalStop: String,
                                  direction: String,
                                  isCancelled: Boolean,
                                  state: Option[String]) extends Departure

case class STrainDeparture(name: String,
                           time: String,
                           date: String,
                           messages: String,
                           track: String,
                           updatedTime: Option[String],
                           updatedDate: Option[String],
                           updatedTrack: Option[String],
                           finalStop: String,
                           direction: String,
                           isCancelled: Boolean,
                           state: Option[String]) extends Departure {

  override def displayName: String = s"$name <i>$direction</i>"
}

case class MetroDeparture(name: String,
                          time: String,
                          date: String,
                          messages: String,
                          track: String,
                          updatedTime: Option[String],
                          updatedDate: Option[String],
                          updatedTrack: Option[String],
                          finalStop: String,
                          direction: String,
                          isCancelled: Boolean,
                          state: Option[String]) extends Departure

case class BusDeparture(name: String,
                        time: String,
                        date: String,
                        messages: String,
                        updatedTime: Option[String],
                        updatedDate: Option[String],
                        finalStop: String,
                        direction: String,
                        isCancelled: Boolean,
                        state: Option[String]) extends Departure


case class GenericDeparture(unknownType: String,
                            name: String,
                            time: String,
                            date: String,
                            messages: String,
                            updatedTime: Option[String],
                            updatedDate: Option[String],
                            finalStop: String,
                            direction: String,
                            isCancelled: Boolean,
                            state: Option[String]) extends Departure