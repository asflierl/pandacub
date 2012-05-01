package eu.flierl.pandacub

import Cells._
import Show.show
import collection.breakOut

abstract class PointsOfInterest(state: BotState, view: View) {
  private val center = Vec(view.len / 2, view.len / 2)
  private val paths = new ShortestPaths(view.graph(state.trailMap), center)
  
  def closest(cell: Cell, status: String) = best(cell, status, _.minBy(_._1)) 
  
  def farthest(cell: Cell, status: String) = best(cell, status, _.maxBy(_._1)) 
  
  val confused = (state, show(Status("*confused*")))
  
  def best(cell: Cell, status: String, select: Seq[(Long, Vec)] => (Long, Vec)): Option[State] = {
    val cells = pathsTo(cell)
    if (cells isEmpty) None
    else move(select, status, cells)
  }
  
  private def pathsTo(cell: Cell): Seq[(Long, Vec)] = 
    (for {
      f <- view.all(cell)
      d <- paths.distanceTo(f)
    } yield (d, f))(breakOut)
  
  private def move(select: Seq[(Long, Vec)] => (Long, Vec), status: String, 
                   cells: Seq[(Long, Vec)]): Some[State] = {
    
    val nextCell = select(cells)._2
    val target = paths.firstStepToVec(nextCell).get.value
    
    val first = Trail(center, (view.len * 3L) / 2L + 2L)
    val trail = first :: state.last.takeWhile(_.discouragement > 0)
    def translate(v: Vec) = v + center - target
    
    val nextTrail = trail map { t =>
      t.copy(cell = translate(t.cell), discouragement = (t.discouragement * 2L) / 3L)
    } 
    
    Some((state.copy(last = nextTrail), show(Move(target - center) +: Status(status))))
  }
}