package eu.flierl.pandacub

import Cells._

case class View(n: Int, area: Map[Vec, Cell])

object View extends (List[Cell] => View){
  def apply(cells: List[Cell]): View = {
    require((cells length) % 2 == 1, "there must be an odd number of cells")
    val n = sqrt(cells length)
    View(n, (for ((cell, index) <- cells.zipWithIndex) yield pair(n, index, cell)) toMap)
  }
  
  private def pair(n: Int, i: Int, c: Cell) = Vec(i % n, i / n) -> c
}