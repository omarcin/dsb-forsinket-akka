package com.oczeretko.dsbforsinket

object RegistrationTag {

  def apiTagToRegistrationTag(tag: String): RegistrationTag = {
    if (tag.startsWith(Tags.stationTagPrefix))
      StationRegistration(tag.substring(Tags.stationTagPrefix.length))
    else if (tag.startsWith(Tags.timeTagPrefix))
      TimeRegistration(tag.substring(Tags.timeTagPrefix.length))
    else
      Registration(tag.split("-")(0), tag.split("-")(1))
  }
}

sealed trait RegistrationTag

case class TimeRegistration(time: String) extends RegistrationTag

case class StationRegistration(station: String) extends RegistrationTag

case class Registration(station: String, time: String) extends RegistrationTag{
  def messageTag = s"$station-$time"
}
