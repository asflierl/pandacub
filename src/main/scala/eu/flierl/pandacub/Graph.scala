package eu.flierl.pandacub

trait Graph {
  def contains(v: Vec): Boolean
  def edge(mx: Vec, my: Vec): Long
  def nodes: Iterable[Vec]
  def neighboursOf(v: Vec): Iterator[(Vec, Long)]
  def edgeCount: Int
}