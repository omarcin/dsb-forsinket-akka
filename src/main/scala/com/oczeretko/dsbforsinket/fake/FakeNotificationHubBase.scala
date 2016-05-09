package com.oczeretko.dsbforsinket.fake

import java.util
import java.util.Date

import com.windowsazure.messaging.{NotificationHubJob, Registration, _}
import org.apache.http.concurrent.FutureCallback


class FakeNotificationHubBase extends INotificationHub {
  override def createOrUpdateInstallation(installation: Installation): Unit = ???

  override def patchInstallationAsync(s: String, list: util.List[PartialUpdateOperation], futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def patchInstallationAsync(s: String, futureCallback: FutureCallback[AnyRef], partialUpdateOperations: PartialUpdateOperation*): Unit = ???

  override def getRegistrationsByChannelAsync(s: String, i: Int, s1: String, futureCallback: FutureCallback[CollectionResult]): Unit = ???

  override def getRegistrationsByChannelAsync(s: String, futureCallback: FutureCallback[CollectionResult]): Unit = ???

  override def getRegistrationAsync(s: String, futureCallback: FutureCallback[Registration]): Unit = ???

  override def sendNotification(notification: Notification, s: String): Unit = ???

  override def sendNotification(notification: Notification): Unit = ???

  override def sendNotification(notification: Notification, set: util.Set[String]): Unit = ???

  override def deleteInstallation(s: String): Unit = ???

  override def deleteRegistration(registration: Registration): Unit = ???

  override def deleteRegistration(s: String): Unit = ???

  override def createRegistrationAsync(registration: Registration, futureCallback: FutureCallback[Registration]): Unit = ???

  override def createOrUpdateInstallationAsync(installation: Installation, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def getAllNotificationHubJobs: util.List[NotificationHubJob] = ???

  override def deleteRegistrationAsync(registration: Registration, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def deleteRegistrationAsync(s: String, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def scheduleNotification(notification: Notification, date: Date): Unit = ???

  override def scheduleNotification(notification: Notification, set: util.Set[String], date: Date): Unit = ???

  override def scheduleNotification(notification: Notification, s: String, date: Date): Unit = ???

  override def scheduleNotificationAsync(notification: Notification, s: String, date: Date, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def scheduleNotificationAsync(notification: Notification, date: Date, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def scheduleNotificationAsync(notification: Notification, set: util.Set[String], date: Date, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def upsertRegistration(registration: Registration): Registration = ???

  override def getInstallation(s: String): Installation = ???

  override def submitNotificationHubJob(notificationHubJob: NotificationHubJob): NotificationHubJob = ???

  override def getRegistrations: CollectionResult = ???

  override def getRegistrations(i: Int, s: String): CollectionResult = ???

  override def submitNotificationHubJobAsync(notificationHubJob: NotificationHubJob, futureCallback: FutureCallback[NotificationHubJob]): Unit = ???

  override def getNotificationHubJob(s: String): NotificationHubJob = ???

  override def upsertRegistrationAsync(registration: Registration, futureCallback: FutureCallback[Registration]): Unit = ???

  override def updateRegistrationAsync(registration: Registration, futureCallback: FutureCallback[Registration]): Unit = ???

  override def createRegistration(registration: Registration): Registration = ???

  override def getAllNotificationHubJobsAsync(futureCallback: FutureCallback[util.List[NotificationHubJob]]): Unit = ???

  override def getRegistration(s: String): Registration = ???

  override def updateRegistration(registration: Registration): Registration = ???

  override def getInstallationAsync(s: String, futureCallback: FutureCallback[Installation]): Unit = ???

  override def getNotificationHubJobAsync(s: String, futureCallback: FutureCallback[NotificationHubJob]): Unit = ???

  override def getRegistrationsByChannel(s: String): CollectionResult = ???

  override def getRegistrationsByChannel(s: String, i: Int, s1: String): CollectionResult = ???

  override def patchInstallation(s: String, partialUpdateOperations: PartialUpdateOperation*): Unit = ???

  override def patchInstallation(s: String, list: util.List[PartialUpdateOperation]): Unit = ???

  override def createRegistrationIdAsync(futureCallback: FutureCallback[String]): Unit = ???

  override def createRegistrationId(): String = ???

  override def getRegistrationsAsync(i: Int, s: String, futureCallback: FutureCallback[CollectionResult]): Unit = ???

  override def sendNotificationAsync(notification: Notification, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def sendNotificationAsync(notification: Notification, set: util.Set[String], futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def sendNotificationAsync(notification: Notification, s: String, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def deleteInstallationAsync(s: String, futureCallback: FutureCallback[AnyRef]): Unit = ???

  override def getRegistrationsByTag(s: String): CollectionResult = ???

  override def getRegistrationsByTag(s: String, i: Int, s1: String): CollectionResult = ???

  override def getRegistrationsByTagAsync(s: String, i: Int, s1: String, futureCallback: FutureCallback[CollectionResult]): Unit = ???

  override def getRegistrationsByTagAsync(s: String, futureCallback: FutureCallback[CollectionResult]): Unit = ???
}
