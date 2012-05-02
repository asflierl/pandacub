package eu.flierl.pandacub

import scala.io.Source
import scalatron.botwar.botPlugin.ControlFunctionFactory

object ProfilingRun extends App {
  val commands = (Source fromFile "src/test/resources/game-log.txt" getLines) toList
  val bot = new ControlFunctionFactory().create
  
  while (true) {
    commands foreach bot
  }
}