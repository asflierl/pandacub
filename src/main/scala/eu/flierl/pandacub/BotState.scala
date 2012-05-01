package eu.flierl.pandacub

import collection.breakOut

case class BotState(last: List[Trail] = List()) {
  lazy val trailMap: Map[Vec, Long] = 
    last.map(t => t.cell -> t.discouragement)(breakOut) 
}

case class Trail(cell: Vec, discouragement: Long)