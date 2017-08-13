import sbt.Keys._

sbtPlugin := true
scalaVersion := "2.12.3"
version := "1.0.0-SNAPSHOT"
organization := "io.github.imarios"
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

publishMavenStyle := true

publishTo := {
   val nexus = "https://oss.sonatype.org/"
   if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
   else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq

resolvers ++= Seq(Resolver.mavenLocal)
