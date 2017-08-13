package io.github.imarios.sbtplugin.artifactory

import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Plugins, Setting, SettingKey, settingKey}
import sbt.Keys.{publishTo, isSnapshot, pomIncludeRepository, publishMavenStyle, publishArtifact, packageDoc, credentials, resolvers, sLog}
import sbt._

/**
  *
  */
object ArtifactoryPlugin extends AutoPlugin {

  object autoImport {
    lazy val artifactoryServerAliveTimeout: SettingKey[Int] =
      settingKey[Int]("Amount of time (in ms) to wait for Artifactory server to reply back.")
    lazy val artifactoryHostname: SettingKey[String] =
      settingKey[String]("Hostname or IP of Artifactory.")
    lazy val artifactoryPort: SettingKey[String] =
      settingKey[String]("Port the Artifactory server is listening to.")
    lazy val artifactoryUsername: SettingKey[String] =
      settingKey[String]("Username for Artifactory server.")
    lazy val artifactoryPassword: SettingKey[String] =
      settingKey[String]("Password for Artifactory server.")
    lazy val artifactoryReleaseRepoName: SettingKey[String] =
      settingKey[String]("The name of the releases repository")
    lazy val artifactorySnapshotRepoName: SettingKey[String] =
      settingKey[String]("The name of the snapshot repository")
    lazy val artifactoryReleaseLabel: SettingKey[String] =
      settingKey[String]("Typically this is called 'Artifactory Realm'")
    lazy val artifactorySnapshotLabel: SettingKey[String] =
      settingKey[String]("Typically this is called 'Artifactory Snapshot Repository'")
    lazy val artifactoryVerifyLocalRepo: SettingKey[Boolean] =
      settingKey[Boolean]("Verify if the system has access the Artifactory server")
    lazy val artifactoryReleaseRepoResolver: SettingKey[Resolver] =
      settingKey[Resolver]("Generates the releases repository resolver")
    lazy val artifactorySnapshotRepoResolver: SettingKey[Resolver] =
      settingKey[Resolver]("Generates the snapshot repository resolver")
    lazy val artifactoryGetLocalRepos: SettingKey[Seq[Resolver]] =
      settingKey[Seq[Resolver]]("Gets the local repositories (local M2 or local Artifactory)")
  }

  import autoImport._


  private def buildRepoUrl(host: String, port: String, repoName: String, protocol: String = "http"): String = {
    s"$protocol://$host:$port/artifactory/$repoName/"
  }

  override def requires: Plugins = JvmPlugin

  override def globalSettings: Seq[Setting[_]] = Nil

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    pomIncludeRepository := Function.const(false),
    artifactoryHostname := "127.0.0.1",
    artifactoryServerAliveTimeout := 1500,
    artifactoryPort := "8081",
    artifactoryUsername := "admin",
    artifactoryPassword := "password",
    artifactoryReleaseRepoName := "libs-release-local",
    artifactorySnapshotRepoName := "libs-snapshot-local",
    artifactoryReleaseLabel := "Artifactory Realm",
    artifactorySnapshotLabel := "Artifactory Snapshot Repository",
    artifactoryReleaseRepoResolver := {
      val host = artifactoryHostname.value
      val port = artifactoryPort.value
      val releasesLabel = artifactoryReleaseLabel.value
      val releasesName = artifactoryReleaseRepoName.value
      releasesLabel at buildRepoUrl(host, port, releasesName)
    },
    artifactorySnapshotRepoResolver := {
      val host = artifactoryHostname.value
      val port = artifactoryPort.value
      val snapshotLabel = artifactorySnapshotLabel.value
      val snapshotName = artifactorySnapshotRepoName.value
      snapshotLabel at buildRepoUrl(host, port, snapshotName)
    },
    publishTo := {
      val snapshots = artifactorySnapshotRepoResolver.value
      val releases = artifactoryReleaseRepoResolver.value
      if (isSnapshot.value) Some(snapshots)
      else Some(releases)
    },
    publishArtifact in(Compile, packageDoc) := false,
    credentials += Credentials(
      artifactoryReleaseLabel.value, artifactoryHostname.value,
      sys.props.getOrElse("ARTIFACTORY_USER", default = artifactoryUsername.value),
      sys.props.getOrElse("ARTIFACTORY_PASS", default = artifactoryPassword.value)),
    artifactoryVerifyLocalRepo := {
      val u = url(s"http://${artifactoryHostname.value}:${artifactoryPort.value}")
      val c = u.openConnection
      c.setConnectTimeout(artifactoryServerAliveTimeout.value)
      scala.util.Try {
        c.getContent
      }.isSuccess
    },
    resolvers ++= artifactoryGetLocalRepos.value,
    artifactoryGetLocalRepos := {
      if (artifactoryVerifyLocalRepo.value) {
        sLog.value.info(s"Found Artifactory server on ${artifactoryHostname.value} at port ${artifactoryPort.value}.")
        Seq(artifactorySnapshotRepoResolver.value, artifactoryReleaseRepoResolver.value, Resolver.mavenLocal)
      } else {
        sLog.value.warn(
          s"Unable to find Artifactory server on ${artifactoryHostname.value} at port ${artifactoryPort.value}. " +
          s"Using local Maven repository instead.")
        Seq(Resolver.mavenLocal)
      }
    })
}