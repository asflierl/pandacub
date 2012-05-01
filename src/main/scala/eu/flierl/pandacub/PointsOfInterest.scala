package eu.flierl.pandacub

import Cells._
import Show.show

abstract class PointsOfInterest(state: BotState, view: View) {
  val center = Vec(view.len / 2, view.len / 2)
  val paths = new ShortestPaths(view.graph(state.trailMap), center)
  
  val closestFood: Option[State] = {
    val food = for {
      f <- (view.all(Bamboo) ++ view.all(Fluppet)).toSeq
      d <- paths.distanceTo(f)
    } yield (d, f)
    
    if (food isEmpty) None
    else move(_.minBy(_._1), "*munch*", food)
  }
  
  def farthest(cell: Cell, status: String): Option[State] = {
    val cells = for {
      c <- view.all(cell).toSeq
      d <- paths.distanceTo(c)
    } yield (d, c)
    
    if (cells isEmpty) None
    else move(_.maxBy(_._1), status, cells)
  }
  
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