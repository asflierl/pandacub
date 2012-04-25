import sbt._
import Keys._
import Project.Setting
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseCreateSrc

object PandaCubBuild extends Build {
  lazy val root = Project(
    id = "pandacub",
    base = file("."),
    settings = Project.defaultSettings ++ botSettings)
    
  def botSettings: Seq[Setting[_]] = Seq(
    version := "1.0",
    organization := "eu.flierl",
    
    scalaVersion := "2.9.2",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-explaintypes"),
      
    EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
    
    scalatronDir := file("Scalatron"),
    
    play <<= (scalatronDir, name, javaOptions, Keys.`package` in Compile) map {
      (base, name, javaOptions, botJar) =>
        IO delete (base / "bots" / name)
        IO copyFile (botJar, base / "bots" / name / "ScalatronBot.jar")
        Process("java" +: (javaOptions ++ Seq("-jar", "Scalatron.jar", "-browser", "no")), base / "bin") !
    },
    
    testOptions := Seq(
      Tests.Filter(_ == "eu.flierl.pandacub.PandaCubSpec"), 
      Tests.Argument("html", "console")),

    testOptions <+= crossTarget map { ct =>
      Tests.Setup { () => 
        System.setProperty("specs2.outDir", (ct / "specs2") absolutePath)
      }
    },

    libraryDependencies ++= Seq(    
      "org.specs2" %% "specs2" % "1.9" % "test",
      "org.scalacheck" %% "scalacheck" % "1.9" % "test",
      "junit" % "junit" % "4.7" % "test",
      "org.pegdown" % "pegdown" % "1.0.2" % "test",
      "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"))

  val scalatronDir = SettingKey[File]("scalatron-dir")
  val play = TaskKey[Unit]("play")
}
