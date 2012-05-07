/*
 * Copyright (c) 2012 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

import sbt._
import Keys._
import Project.Setting
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseCreateSrc
import Keys.{ `package` => pack }

object PandaCubBuild extends Build {
  lazy val root = Project(
    id = "pandacub",
    base = file("."),
    settings = Project.defaultSettings 
            ++ botSettings
            ++ addArtifact(Artifact("pandacub", "zip", "zip"), release).settings)
    
  def botSettings: Seq[Setting[_]] = Seq(
    version := "3.3.2",
    organization := "eu.flierl",
    
    scalaVersion := "2.9.2",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-explaintypes"),
      
    EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
    
    release <<= (pack in Compile, crossTarget, name, version, unmanagedSources in Compile) map { 
      (jar, target, name, version, sources) =>
        val zip = target / "%s-%s.zip".format(name, version)
        val heapSrc = sources find (_.name == "FibonacciHeap.java") get
        
        IO.zip(
          Seq(
            (jar, name + "/ScalatronBot.jar"), 
            (file("license.txt"), "license.txt"), 
            (heapSrc, heapSrc name)), 
          zip)
          
        zip
    },
    
    scalatronDir := file("Scalatron"),
    
    javaOptions ++= Seq("-server", "-Xmx2g", "-XX:+TieredCompilation", 
      "-XX:Tier2CompileThreshold=150000", "-XX:CompileThreshold=1500", "-XX:+AggressiveOpts"),
    
    play <<= (scalatronDir, name, javaOptions, pack in Compile) map {
      (base, name, javaOptions, botJar) =>
        require(base exists, "The setting '%s' must point to the base directory of an existing " +
      		"Scalatron installation.".format(scalatronDir.key.label))
        IO delete (base / "bots" / name)
        IO copyFile (botJar, base / "bots" / name / "ScalatronBot.jar")
        Process("java" +: (javaOptions ++ Seq("-jar", "Scalatron.jar", "-browser", "no",
          "-x", "100", "-y", "100", "-steps", "5000")), base / "bin") !
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

  val scalatronDir = SettingKey[File]("scalatron-dir", "base directory of an existing Scalatron installation")
  val play = TaskKey[Unit]("play", "recompiles, packages and installs the bot, then starts Scalatron")
  val release = TaskKey[File]("release", "zips the assembled product along with the license")
}
