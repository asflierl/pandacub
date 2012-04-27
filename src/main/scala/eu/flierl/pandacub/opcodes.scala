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

case class Spawn(direction: Vec, name: String, Energy: Int)

case class Explode(size: Int)

case class Say(text: String)

case class Status(text: String)

case class Log(text: String)

case class Vec(x: Int, y: Int)