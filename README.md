# sbt-artifactory

An sbt plugin to ease working with Artifactory. Currently cross published for `sbt 0.13.x and 1.0.x`. 

This plugin is trying to solve the following:
* Gives default repository name for Artifactory and simplifies your `build.sbt` file
* If for any reason you cannot access the Artifactory server (e.g., you are outside the VPN) 
it will detect this quickly and will not add Artifactory to your set of resolvers and save you
tone of wasted time

To use the plugin in your sbt project, add the following under `project/plugins.sbt`.

```scala
addSbtPlugin("io.github.imarios" % "sbt-artifactory" % "1.0.0-SNAPSHOT")
resolvers ++= Seq("Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
```


A minimal `build.sbt` using this plugin:

```scala
enablePlugins(ArtifactoryPlugin)
artifactoryPort := "8081"
artifactoryHost := "localhost"
```

running `sbt publish` will publish the artifacts to Artifactory. 

```scala
sbt> show artifactoryReleaseRepoResolver
[info] Artifactory Realm: http://localhost:8081/artifactory/libs-release-local/
sbt> show artifactorySnapshotRepoResolver
[info] Artifactory Snapshot Repository: http://localhost:8081/artifactory/libs-snapshot-local/
```

This assumes you already have configured Artifactory to have libs-snapshot-local,libs-release-local 
as valid sbt repositories. [See instructions here](https://www.jfrog.com/confluence/display/RTF/SBT+Repositories).
