package eu.flierl.pandacub

import scala.collection.mutable.BitSet
import Vec.Ord

class MatrixGraph(viewLen: Int) extends Graph {
  import MatrixGraph.directions
  
  private[this] val matrixLen = viewLen * viewLen
  private[this] val matrixCells = matrixLen * matrixLen
  private[this] val matrix = makeMatrix
  private[this] val nodeSet = BitSet()

  @inline private[this] def makeMatrix: Array[Long] = {
    val m = Array.ofDim[Long](matrixCells)
    var i = 0
    while (i < matrixCells) {
      m(i) = -1L
      i += 1
    }
    m
  }
  
  def contains(v: Vec): Boolean = nodeSet contains cell(v)
  
  def add(mx: Vec, my: Vec, weight: Long): Unit = {
    val i = idx(min(mx, my), max(mx, my))
    matrix(i) = weight
    nodeSet += cell(mx)
    nodeSet += cell(my)
  }
  
  def edge(mx: Vec, my: Vec): Long = {
    val i = idx(min(mx, my), max(mx, my))
    if (i < 0 || i >= matrix.length) -1L
    else matrix(i)
  }
  
  def nodes: Iterable[Vec] = nodeSet.view map rev
  
  def neighboursOf(v: Vec) = directions.view map (v+) map (t => (t, edge(v, t))) filter (_._2 >= 0) iterator
  
  def edgeCount = matrix count (0<=)

  private[this] def cell(v: Vec): Int = v.y * viewLen + v.x
  private[this] def rev(i: Int): Vec = Vec(i % viewLen, i / viewLen)
  private[this] def idx(mx: Vec, my: Vec): Int = cell(my) * matrixLen + cell(mx)
  private[this] def min(mx: Vec, my: Vec): Vec = if (Ord.compare(mx, my) <= 0) mx else my
  private[this] def max(mx: Vec, my: Vec): Vec = if (Ord.compare(mx, my) > 0) mx else my
}
object MatrixGraph {
  private[MatrixGraph] val directions = 
    List(Vec( 1, 0), Vec(0,  1), Vec( 1,  1), Vec( 1, -1),
         Vec(-1, 0), Vec(0, -1), Vec(-1, -1), Vec(-1,  1))
         
  def apply(len: Int, edges: (Vec, Vec, Long)*): MatrixGraph = {
    val g = new MatrixGraph(len)
    edges foreach { case (mx, my, w) => g add (mx, my, w) }
    g
  }
}