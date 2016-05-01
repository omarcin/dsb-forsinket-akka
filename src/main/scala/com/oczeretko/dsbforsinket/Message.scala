package com.oczeretko.dsbforsinket

object Message {

  case object InitSystem

  case object FindSubscribers

  case class CheckForDelay(messageTag : String, station: String, time : String, isTest : Boolean)

  case class Notify(messageTag: String, data: Map[String, String])

}
