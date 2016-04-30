package com.oczeretko.dsbforsinket

object Tags {
  private val buckets : Int = 10

  val stationTagPrefix : String = "station-"
  val timeTagPrefix : String = "time-"

  def bucketsFromTag (tag : String) : Seq[String] = Seq.range(0, buckets).map(num => s"$tag$num")
}
