# sbt-artifactory

An sbt plugin to ease working with Artifactory. Currently cross published for `sbt 0.13.x and 1.0.x`. 

This plugin offers the following
* Reasonable default settings for Artifactory that simplify your `build.sbt` file
* Same settings for both publishing and resolving artifacts to/from Artifactory
* Detects when the Artifactory server is not accessible and proberly adjusts your project's resolves 
(saving a lot of time querying the server and timing-out)

## Getting started

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

running `sbt publish` will publish the artifacts to Artifactory (read below if the credentials are not the defaults). 

```scala
sbt> show artifactoryReleaseRepoResolver
[info] Artifactory Realm: http://localhost:8081/artifactory/libs-release-local/
sbt> show artifactorySnapshotRepoResolver
[info] Artifactory Snapshot Repository: http://localhost:8081/artifactory/libs-snapshot-local/
```

This assumes you already have configured Artifactory to have libs-snapshot-local,libs-release-local 
as valid sbt repositories. [See instructions here](https://www.jfrog.com/confluence/display/RTF/SBT+Repositories).

## Publishing with different username/password

The default username/password for artifactory is admin/password. If your server has these defaults, then everything should work as described above. If your server has different credentials (which is probably the smart thing to do), then this is the suggest way of publishing your artifacts:

```bash
ARTIFACTORY_USER="add your user name here" ARTIFACTORY_PASS="add your password here" sbt publish
```
