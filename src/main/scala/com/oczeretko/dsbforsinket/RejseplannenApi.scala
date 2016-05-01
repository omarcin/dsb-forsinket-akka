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
      .build();

  lazy val service: RejseplanenService = retrofit.create(classOf[RejseplanenService]);

  def getDelayedDepartures(station: String)(implicit context: ExecutionContext): Future[List[RejseplanenService.Departure]] = {
    val promise = Promise[RejseplanenService.DepartureBoard]
    service.getDepartures(station).enqueue(promise)
    for {
      result <- promise.future
      departures = result.getDepartures.toList
    } yield departures
  }
}
