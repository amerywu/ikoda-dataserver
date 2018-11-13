import sbt.ExclusionRule

name := """ikoda-dataserver"""
organization := "ikoda"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

resolvers += "Local Maven Repository" at "file://C:/Users/jake/.m2/repository"

libraryDependencies += guice
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.3.2"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.0"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "1.0-M1"



//<groupId></groupId>
 // <artifactId>cassandra-driver-core</artifactId>

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ikoda.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ikoda.binders._"
