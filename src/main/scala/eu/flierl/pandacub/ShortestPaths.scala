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
  private val dist = Map[Vec, Long]()
  private val previous = Map[Vec, Vec]()
  private val from = (graph get center).value
  
  dist.put(from, 0)
  
  private val q = new TreeSet(Ord)
  
  q addAll graph.nodes.map(_.value).asJava
  
  while (! q.isEmpty && dist.contains(q.first)) {
    val closest = q.pollFirst
    val closestNode = graph get closest
    val outEdges = closestNode.outgoing.iterator
    
    while (outEdges hasNext) {
      val edge = outEdges.next
      val neighbour = (if (edge.head == closestNode) edge.last else edge.head).value
      
      if (q contains neighbour) {
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
  }
  
  def distanceTo(v: Vec): Option[Long] = graph find v flatMap (n => dist.get(n.value))
      
  @tailrec
  def firstStepTo(n: Vec): Option[Vec] =
    if (! previous.contains(n)) None
    else if (previous(n) == from) Some(n)
    else firstStepTo(previous(n))
    
  @tailrec
  def pathTo(n: Vec, p: List[Vec] = List()): List[Vec] =
    if (! previous.contains(n)) p
    else pathTo(previous(n), n :: p)
    
  private[this] object Ord extends Comparator[Vec] {
    private[this] val inf = Int.MaxValue.toLong
    
    def compare(a: Vec, b: Vec) = {
      val weights = implicitly[Ordering[Long]].compare(grab(a), grab(b))
      
      if (weights != 0) weights
      else implicitly[Ordering[Vec]].compare(a, b)
    }
    
    @inline private[this] def grab(a: Vec) = {
      dist.getOrElse(a, inf)
    }
  }
}
