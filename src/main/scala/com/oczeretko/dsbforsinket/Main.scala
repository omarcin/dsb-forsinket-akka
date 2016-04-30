package com.oczeretko.dsbforsinket

import akka.actor.{ActorSystem, Props}

object Main {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("dsb-forsinket")
    val initActor = system.actorOf(Props[InitActor])

    initActor ! Message.InitSystem

    println("ENTER to close")
    scala.io.StdIn.readLine()
    system.terminate()
  }
}




