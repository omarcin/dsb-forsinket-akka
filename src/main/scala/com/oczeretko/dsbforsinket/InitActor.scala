package com.oczeretko.dsbforsinket

import java.time.LocalTime

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

class InitActor extends Actor with ActorLogging {

  val manager = context.actorOf(Props[DeparturesManagerActor], "manager")

  def receive: Receive = {
    case Message.InitSystem => {
      log.info("Initializing")

      val minutesToRound = 5 - (LocalTime.now().getMinute + 1) % 5
      log.info(s"First schedule in $minutesToRound minutes")

      context.system.scheduler.schedule(
        minutesToRound minutes,
        5 minutes,
        manager,
        Message.FindSubscribers)(context.dispatcher)

      // TODO: temporary
      log.info("Sending one message immediately")
      manager ! Message.FindSubscribers
    }
  }
}
