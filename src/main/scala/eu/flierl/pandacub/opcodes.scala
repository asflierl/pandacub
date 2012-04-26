package eu.flierl.pandacub

import java.io.File

case class Welcome(name: String, path: File, apocalypse: Int, round: Int)

sealed trait React {
  def generation: Int
  def entity: String
  def time: Int
  def view: String
  def energy: Int
}

case class MasterReact(generation: Int, entity: String, time: Int, view: String,
  energy: Int) extends React
  
case class MiniReact(generation: Int, entity: String, time: Int, view: String,
  energy: Int, master: Vec) extends React

case class Goodbye(energy: Int)
  
case class Vec(x: Int, y: Int)