package com.oczeretko.dsbforsinket

object Message {

  case object InitSystem

  case object FindSubscribers

  case class CheckForDelay(registration: Registration, isTest : Boolean)

  case class Notify(messageTag: String, data: Map[String, String])

  case class GetRegistrationTagsForTag(tag: String)
  case class RegistrationTagsForTag(tag: String, registrationTags: List[Registration])

}
