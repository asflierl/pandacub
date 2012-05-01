package eu.flierl.pandacub

import Cells._
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.WUnDiEdge
import collection.breakOut

final case class View(len: Int, area: Map[Vec, Cell]) {
  lazy val inverse = area.groupBy(_._2).mapValues(_.keySet)
  
  def all(c: Cell): Set[Vec] = inverse.getOrElse(c, Set())
  
  def graph(last: Map[Vec, Long] = Map()): G = (for {
    v <- area.keys filter isSafe
    n <- southEastNeighboursOf(v)
  } yield v ~ n % last.get(v).orElse(last.get(n)).getOrElse(1L))(breakOut)
  
  private[this] def southEastNeighboursOf(v: Vec): Seq[Vec] =
    List(Vec(1, 0), Vec(0, 1), Vec(1, 1), Vec(1, -1)) map (v+) filter area.contains filter isSafe
  
  def isSafe(v: Vec) = area(v) match {
    case Wall | Tiger | Kitty | Shroom | Snorg => false
    case _ => true
  }
  
  override def toString = Show.show(this).sliding(len, len).mkString("\n")
}

object View extends (List[Cell] => View) {
  def apply(cells: List[Cell]): View = {
    require((cells length) % 2 == 1, "there must be an odd number of cells")
    val n = sqrt(cells length)
    View(n, (for ((cell, index) <- cells.zipWithIndex) yield pair(n, index, cell)) toMap)
  }
  
  private def pair(n: Int, i: Int, c: Cell) = Vec(i % n, i / n) -> c
}