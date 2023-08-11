name := "LogService"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)