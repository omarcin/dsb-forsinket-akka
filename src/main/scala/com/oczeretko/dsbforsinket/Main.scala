package com.oczeretko.dsbforsinket

import akka.actor.ActorSystem

object Main {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem("dsb-forsinket")

    println("ENTER to close")
    scala.io.StdIn.readLine()
    system.terminate()
  }
}


