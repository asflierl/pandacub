package eu.flierl.pandacub

import java.io.File

case class Welcome(name: String, path: File, apocalypse: Int, round: Int)

sealed trait React

case class MasterReact(generation: Int, entity: String, time: Int, view: String,
  energy: Int) extends React
  
case class MiniReact(generation: Int, entity: String, time: Int, view: String,
  energy: Int, master: Vec) extends React

case class Goodbye(energy: Int)
  
case class Vec(x: Int, y: Int) {
  override def toString = x.toString + ':' + y.toString
}
object Vec {
  def apply(s: String): Vec = { val c = s.split(':'); Vec(c.head.toInt, c.tail.tail.head.toInt) }
}