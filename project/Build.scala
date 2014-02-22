import sbt._, Keys._
import sbtrelease._
import ReleaseStateTransformations._
import com.typesafe.sbt.pgp.PgpKeys
import xerial.sbt.Sonatype._

object build extends Build {

  lazy val sonatypeReleaseAllTask = taskKey[Unit]("sonatypeReleaseAllTask")

  lazy val baseSettings = ReleasePlugin.releaseSettings ++ sonatypeSettings ++ Seq(
    scalaVersion := "2.10.3",
    crossScalaVersions := List("2.9.3", "2.10.3"),
    organization := "com.github.xuwei-k",
    scmInfo := Some(ScmInfo(
      url("https://github.com/xuwei-k/sbt-sonatype-and-sbt-release-sample"),
      "scm:git:git@github.com:xuwei-k/sbt-sonatype-and-sbt-release-sample.git"
    )),
    description := "sbt-sonatype and sbt-release plugin sample",
    pomExtra := (
      <url>https://github.com/xuwei-k/sbt-sonatype-and-sbt-release-sample</url>
      <developers>
        <developer>
          <id>xuwei-k</id>
          <name>Kenji Yoshida</name>
          <url>https://github.com/xuwei-k</url>
        </developer>
      </developers>
    ),
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    scalacOptions ++= Seq("-deprecation", "-Xlint", "-unchecked"),
    publishTo := {
      if(isSnapshot.value)
        Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
      else
        Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    },
    sonatypeReleaseAllTask := SonatypeKeys.sonatypeReleaseAll.toTask("").value,
    ReleasePlugin.ReleaseKeys.releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(
        action = { state =>
          val extracted = Project extract state
          extracted.runAggregated(PgpKeys.publishSigned in Global in extracted.get(thisProjectRef), state)
        },
        enableCrossBuild = true
      ),
      setNextVersion,
      commitNextVersion,
      ReleaseStep{ state =>
        val extracted = Project extract state
        extracted.runAggregated(sonatypeReleaseAllTask in Global in extracted.get(thisProjectRef), state)
      },
      pushChanges
    )
  )

  lazy val core = Project(
    "example-core", file("core")
  ).settings(baseSettings: _*)

  lazy val extra = Project(
    "example-extra", file("extra")
  ).settings(baseSettings: _*).dependsOn(core)

  lazy val root = Project(
    "root", file(".")
  ).settings(baseSettings: _*).settings(
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  ).aggregate(core, extra)

}

