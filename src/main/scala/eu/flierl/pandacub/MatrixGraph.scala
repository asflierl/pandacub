package eu.flierl.pandacub

import scala.collection.mutable.BitSet

class MatrixGraph(viewLen: Int) {
  private[this] val matrixLen = viewLen * viewLen
  private[this] val matrixCells = matrixLen * matrixLen
  private[this] val matrix = Array.fill(matrixCells)(-1L)
  private[this] val nodes = BitSet()
  
  def contains(v: Vec): Boolean = contains(v.x, v.y)
  
  def contains(x: Int, y: Int): Boolean = nodes contains cell(x, y)
  
  def edge(mx: Vec, my: Vec): Option[Long] = {
    val i = idx(mx, my)
    if (i < 0 || i >= matrix.length || matrix(i) == -1L) None
    else Some(matrix(i))
  }
  
  private[this] def cell(v: Vec): Int = cell(v.x, v.y)
  private[this] def cell(x: Int, y: Int): Int = y * viewLen + x
  private[this] def idx(mx: Vec, my: Vec): Int = cell(my) * matrixLen + cell(mx)
}