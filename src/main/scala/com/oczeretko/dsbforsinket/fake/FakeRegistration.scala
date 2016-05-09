package com.oczeretko.dsbforsinket.fake

import java.util
import com.windowsazure.messaging.Registration
import scala.collection.JavaConversions._

case class FakeRegistration(tagList: List[String]) extends Registration {
  override def getXml: String = ""

  override def getTags: util.Set[String] = new util.HashSet[String](tagList)
}
