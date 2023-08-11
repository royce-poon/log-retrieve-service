name := "LogService"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)