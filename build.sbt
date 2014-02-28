import sbtrelease._
import ReleaseStateTransformations._

releaseSettings

sonatypeSettings

scalaVersion := "2.10.3"

crossScalaVersions := List("2.9.3", "2.10.3")

name := "sbt-sonatype-and-sbt-release-sample"

organization := "com.github.xuwei-k"

scmInfo := Some(ScmInfo(
  url("https://github.com/xuwei-k/sbt-sonatype-and-sbt-release-sample"),
  "scm:git:git@github.com:xuwei-k/sbt-sonatype-and-sbt-release-sample.git"
))

description := "sbt-sonatype and sbt-release plugin sample"

pomExtra := (
<url>https://github.com/xuwei-k/sbt-sonatype-and-sbt-release-sample</url>
<developers>
  <developer>
    <id>xuwei-k</id>
    <name>Kenji Yoshida</name>
    <url>https://github.com/xuwei-k</url>
  </developer>
</developers>
)

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

scalacOptions ++= Seq("-deprecation", "-Xlint", "-unchecked")

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(
    action = state => Project.extract(state).runTask(PgpKeys.publishSigned, state)._1,
    enableCrossBuild = true
  ),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(state => Project.extract(state).runTask(SonatypeKeys.sonatypeReleaseAll, state)._1),
  pushChanges
)

