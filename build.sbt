import NativePackagerHelper._

name := "dsbforsinket"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.4",
  "com.squareup.retrofit2" % "retrofit" % "2.0.2",
  "com.squareup.retrofit2" % "converter-gson" % "2.0.2"
)

mappings in Universal ++= {
    directory("scripts") ++
    contentOf("src/main/resources").toMap.mapValues("config/" + _)
}

enablePlugins(JavaServerAppPackaging)

scriptClasspath := Seq("../config/") ++ scriptClasspath.value

cancelable in Global := true
mainClass in Compile := Some("com.oczeretko.dsbforsinket.Main")
scalacOptions ++= Seq("-feature", "-language:implicitConversions")
