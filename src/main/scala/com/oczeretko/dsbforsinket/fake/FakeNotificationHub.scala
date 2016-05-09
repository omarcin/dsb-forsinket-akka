package com.oczeretko.dsbforsinket.fake

import java.util
import java.util.concurrent.TimeUnit

import com.oczeretko.dsbforsinket.ImplicitConversions
import com.oczeretko.dsbforsinket.fake.FakeNotificationHubService.FakeNotificationModel
import com.windowsazure.messaging._
import okhttp3.OkHttpClient
import org.apache.http.concurrent.FutureCallback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Promise}

case class FakeNotificationHub(baseUrl: String)(implicit ec: ExecutionContext) extends FakeNotificationHubBase {

  import ImplicitConversions._

  lazy val retrofit: Retrofit =
    new Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
      .client(new OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build())
      .build()

  lazy val service: FakeNotificationHubService = retrofit.create(classOf[FakeNotificationHubService])

  override def sendNotificationAsync(notification: Notification, tagExpression: String, callback: FutureCallback[AnyRef]): Unit = {
    val body = notification.getBody
    val tag = tagExpression
    val promise = Promise[String]
    service.postNofication(new FakeNotificationModel(tag, body)).enqueue(promise)

    promise.future onSuccess { case _ => callback.completed(null) }
    promise.future onFailure { case t => callback.failed(new Exception(t)) }
  }

  override def getRegistrationsByTagAsync(tag: String, futureCallback: FutureCallback[CollectionResult]): Unit = {
    val promise = Promise[util.ArrayList[FakeNotificationHubService.FakeRegistrationModel]]
    service.getRegistrations(tag).enqueue(promise)

    val futureRegistrations = promise.future.map(res => res.toList.map(rm => FakeRegistration(rm.getTags.toList)))

    futureRegistrations onSuccess {
      case registrations: List[FakeRegistration] =>
        val collectionResult = new CollectionResult()
        registrations.foreach(collectionResult.addRegistration)
        futureCallback.completed(collectionResult)
    }

    futureRegistrations onFailure {
      case t: Throwable => futureCallback.failed(new Exception(t))
    }
  }
}


