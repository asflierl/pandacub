package eu.flierl.pandacub

import collection.mutable.Map
import collection.mutable.Set
import java.util.TreeSet
import java.util.Comparator
import collection.JavaConverters._
import scala.annotation.tailrec

final class ShortestPaths(val graph: G, center: Vec) {
  private val dist = Map[graph.NodeT, Long]()
  private val previous = Map[graph.NodeT, graph.NodeT]()
  
  private val from = graph get center
  
  dist.put(from, 0)
  
  private val q = new TreeSet(Ord)
  
  q addAll graph.nodes.asJava
  
  while (! q.isEmpty && dist.contains(q.first)) {
    val closest = q.pollFirst

    for { 
      neighbour <- (closest outNeighbors) if q contains neighbour
      edge <- closest outgoingTo neighbour
    } {
      val newDistance = dist(closest) + edge.weight 
      
      if (((dist contains neighbour) && newDistance < dist(neighbour)) 
          || ! (dist contains neighbour)) {
        q remove neighbour
        dist.put(neighbour, newDistance)
        previous.put(neighbour, closest)
        q add neighbour
      }
    }
  }
  
  object Ord extends Comparator[graph.NodeT] {
    private[this] val inf = Int.MaxValue.toLong
    
    def compare(a: graph.NodeT, b: graph.NodeT) = {
      val weights = implicitly[Ordering[Long]].compare(
        dist.getOrElse(a, inf), dist.getOrElse(b, inf))
      
      if (weights != 0) weights
      else implicitly[Ordering[Vec]].compare(a.value, b.value)
    }
  }
  
  def distanceTo(n: Vec): Option[Long] = graph find n flatMap dist.get
      
  def firstStepToVec(n: Vec): Option[graph.NodeT] =
    graph find n flatMap firstStepTo
    
  @tailrec
  def firstStepTo(n: graph.NodeT): Option[graph.NodeT] =
    if (! previous.contains(n)) None
    else if (previous(n) == from) Some(n)
    else firstStepTo(previous(n))
}
