package eu.flierl.pandacub

import Cells._
import Show.show

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._

final class Panda(state: BotState) {
  def react(time: Int, view: View, energy: Int): State = {
    val center = Vec(view.len / 2, view.len / 2)
    val paths = new ShortestPaths(dontLookBack(view.graph, center), center)
    
//    println("\n")
//    println(view.toString)
//    println("\n")
    
    closestFood(view, center, paths).orElse(
      farthest(Fog, "exploring", view, center, paths)).orElse(
      farthest(Empty, "roaming", view, center, paths)).getOrElse(
      (state, show(Status("nowhere to go"))))
  }
  
  def closestFood(view: View, center: Vec, paths: ShortestPaths): Option[State] = {
    val food = for {
      f <- (view.all(Bamboo) ++ view.all(Fluppet)).toSeq
      d <- paths.distanceTo(f)
    } yield (d, f)
    
    if (food isEmpty) None
    else move(_.minBy(_._1), "*munch*", food, center, paths)
  }
  
  def farthest(cell: Cell, status: String, view: View, center: Vec, paths: ShortestPaths): Option[State] = {
    val cells = for {
      c <- view.all(cell).toSeq
      d <- paths.distanceTo(c)
    } yield (d, c)
    
    if (cells isEmpty) None
    else move(_.maxBy(_._1), status, cells, center, paths)
  }
  
  def move(order: Seq[(Long, Vec)] => (Long, Vec), status: String, 
           cells: Seq[(Long, Vec)], center: Vec, paths: ShortestPaths): Some[State] = {
    val nextCell = order(cells)._2
    val target = paths.firstStepToVec(nextCell).get.value
    Some((state.copy(last = Some(center + center - target)), 
         show(Move(target - center) +: Status(status))))
  }
  
  def dontLookBack(graph: G, center: Vec): G = state.last.flatMap { last =>
    if (! (graph contains last)) None
    else graph.get(center).outgoingTo(graph.get(last)).headOption.map { edge =>
      graph - edge + (center ~ last % 31L)
    }
  }.getOrElse(graph)
}