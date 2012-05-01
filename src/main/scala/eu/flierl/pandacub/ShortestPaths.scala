/*
 * Copyright (c) 2012 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.flierl.pandacub

import collection.mutable.Map
import collection.mutable.Set
import java.util.TreeSet
import java.util.Comparator
import collection.JavaConverters._
import scala.annotation.tailrec

/** Implements Dijkstra's algorithm on top of an ordered tree, O(V * log(V) + E). */
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
        dist put (neighbour, newDistance)
        previous put (neighbour, closest)
        q add neighbour
      }
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
    
  private[this] object Ord extends Comparator[graph.NodeT] {
    private[this] val inf = Int.MaxValue.toLong
    
    def compare(a: graph.NodeT, b: graph.NodeT) = {
      val weights = implicitly[Ordering[Long]].compare(
        dist.getOrElse(a, inf), dist.getOrElse(b, inf))
      
      if (weights != 0) weights
      else implicitly[Ordering[Vec]].compare(a.value, b.value)
    }
  }
}
