package com.oczeretko.dsbforsinket

object NotificationTag {

  def apiTagToRegistrationTag(tag: String): NotificationTag = {
    if (tag.startsWith(Tags.stationTagPrefix))
      StationTag(tag.substring(Tags.stationTagPrefix.length))
    else if (tag.startsWith(Tags.timeTagPrefix))
      TimeTag(tag.substring(Tags.timeTagPrefix.length))
    else
      RegistrationTag(tag.split("-")(0), tag.split("-")(1))
  }
}

sealed trait NotificationTag {
  def messageTag: String
}

case class TimeTag(time: String) extends NotificationTag {
  override def messageTag: String = Tags.timeTagPrefix + time
}

case class StationTag(station: String) extends NotificationTag {
  override def messageTag: String = Tags.stationTagPrefix + station
}

case class RegistrationTag(station: String, time: String) extends NotificationTag {
  def messageTag = s"$station-$time"
}
