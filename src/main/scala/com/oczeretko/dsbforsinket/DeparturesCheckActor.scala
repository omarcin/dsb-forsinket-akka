package com.oczeretko.dsbforsinket

import akka.actor.Actor

class DeparturesCheckActor extends Actor{
  def receive = {
    case msg : Message.CheckForDelay => {
      println(msg)
    }
  }
}
