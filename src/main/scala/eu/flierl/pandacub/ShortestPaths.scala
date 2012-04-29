package eu.flierl.pandacub

import collection.mutable.Map
import collection.mutable.Set
import java.util.TreeSet
import java.util.Comparator
import collection.JavaConverters._
import scala.annotation.tailrec

final class ShortestPaths (graph: G, from: N) {
  private val dist = Map[N, Long]()
  private val previous = Map[N, N]()
  private val inf = Long.MaxValue
  
  dist.put(from, 0)
  
  private val q = new TreeSet(new Comparator[N] {
    def compare(a: N, b: N) = dist.getOrElse(a, inf).compare(dist.getOrElse(b, inf)) 
  })
  
  q addAll graph.nodes.asJava
  
  while (! q.isEmpty && dist.contains(q.first)) {
    val u = q.pollFirst
    
    for (v <- u.outNeighbors if q.contains(v)) {
      val alt = dist(u) + 1L //FIXME use weight
      if ((dist contains v) && alt < dist(v)) {
        q remove v
        dist.put(v, alt)
        previous.put(v, u)
        q add v
      } else {
        dist.put(v, alt)
        previous.put(v, u)
        q add v
      }
    }
  }
  
  lazy val distances = collection.immutable.Map() ++ dist
  
  def distanceTo(n: N): Option[Long] = dist.get(n)
  
  @tailrec
  def pathTo(n: N, p: List[N] = List()): List[N] =
    if (! previous.contains(n)) p
    else pathTo(previous(n), n :: p)
    
  @tailrec
  def firstStepTo(n: N): Option[N] =
    if (! previous.contains(n)) None
    else if (previous(n) == from) Some(n)
    else firstStepTo(previous(n))
}
