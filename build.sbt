sbtPlugin := true
scalaVersion := "2.12.3"
version := "1.0.0-SNAPSHOT"
organization := "io.mi"
name := "sbt-artifactory"

publishArtifact in(Compile, packageBin) := true
publishArtifact in(Test, packageBin) := false
publishArtifact in(Test, packageSrc) := false
publishArtifact in(Compile, packageDoc) := false
publishArtifact in(Compile, packageSrc) := true

lazy val scalajHttp = "org.scalaj" %% "scalaj-http" % "2.3.0"
lazy val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.5"

libraryDependencies ++= Seq(scalajHttp, slf4jSimple % "provided")

crossSbtVersions := Vector("1.0.0", "0.13.16")

resolvers ++= Seq(Resolver.mavenLocal)
