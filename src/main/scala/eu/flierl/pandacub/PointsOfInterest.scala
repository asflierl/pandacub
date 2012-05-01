package eu.flierl.pandacub

import Cells._
import Show.show
import collection.breakOut

abstract class PointsOfInterest(state: BotState, view: View) {
  val center = Vec(view.len / 2, view.len / 2)
  val paths = new ShortestPaths(view.graph(state.trailMap), center)
  
  def closest(cell: Cell, status: String): Option[State] = best(cell, status, _.minBy(_._1)) 
  
  def farthest(cell: Cell, status: String): Option[State] = best(cell, status, _.maxBy(_._1)) 
  
  val confused = (state, show(Status("*confused*")))
  
  def best(cell: Cell, status: String, order: Seq[(Long, Vec)] => (Long, Vec)): Option[State] = {
    val cells = pathsTo(cell)
    if (cells isEmpty) None
    else move(order, status, cells)
  }
  
  def pathsTo(cell: Cell): Seq[(Long, Vec)] = (for {
      f <- view.all(cell)
      d <- paths.distanceTo(f)
    } yield (d, f))(breakOut)
  
  def move(order: Seq[(Long, Vec)] => (Long, Vec), status: String, 
           cells: Seq[(Long, Vec)]): Some[State] = {
    val nextCell = order(cells)._2
    val target = paths.firstStepToVec(nextCell).get.value
    Some((state.copy(last = (Trail(center) :: state.last.take(15)) map { t =>
      t.copy(cell = t.cell + center - target, 
            discouragement = t.discouragement - 2L)
    }), show(Move(target - center) +: Status(status))))
  }
}