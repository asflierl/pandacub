package eu.flierl.pandacub

import java.io.File

sealed trait OpcodeFromServer

case class Welcome(name: String, path: File, apocalypse: Int, round: Int) extends OpcodeFromServer

sealed trait React extends OpcodeFromServer {
  def generation: Int
  def name: String
  def time: Int
  def view: String
  def energy: Int
}

case class MasterReact(generation: Int, name: String, time: Int, view: String,
  energy: Int) extends React
  
case class MiniReact(generation: Int, name: String, time: Int, view: String,
  energy: Int, master: Vec) extends React

case class Goodbye(energy: Int) extends OpcodeFromServer

sealed trait OpcodeFromBot {
  def +: (first: OpcodeFromBot) = first :: this :: Nil  
}

case class Move(direction: Vec) extends OpcodeFromBot

case class Spawn(direction: Vec, name: String, energy: Int) extends OpcodeFromBot

case class Explode(size: Int) extends OpcodeFromBot

case class Say(text: String) extends OpcodeFromBot

case class Status(text: String) extends OpcodeFromBot

case class Log(text: String) extends OpcodeFromBot

case class Vec(x: Int, y: Int)