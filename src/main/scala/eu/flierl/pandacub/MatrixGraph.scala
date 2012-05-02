package eu.flierl.pandacub

import scala.collection.mutable.BitSet
import Vec.Ord

class MatrixGraph(viewLen: Int) {
  private[this] val matrixLen = viewLen * viewLen
  private[this] val matrixCells = matrixLen * matrixLen
  private[this] val matrix = Array.fill(matrixCells)(-1L)
  private[this] val nodes = BitSet()
  
  def contains(v: Vec): Boolean = nodes contains cell(v)
  
  def add(mx: Vec, my: Vec, weight: Long): Unit = {
    val i = idx(min(mx, my), max(mx, my))
    matrix(i) = weight
    nodes += i
  }
  
  def edge(mx: Vec, my: Vec): Long = {
    val i = idx(min(mx, my), max(mx, my))
    if (i < 0 || i >= matrix.length) -1L
    else matrix(i)
  }

  private[this] def cell(v: Vec): Int = v.y * viewLen + v.x
  private[this] def rev(i: Int): Vec = Vec(i % viewLen, i / viewLen)
  private[this] def idx(mx: Vec, my: Vec): Int = cell(my) * matrixLen + cell(mx)
  private[this] def min(mx: Vec, my: Vec): Vec = if (Ord.compare(mx, my) <= 0) mx else my
  private[this] def max(mx: Vec, my: Vec): Vec = if (Ord.compare(mx, my) > 0) mx else my
}