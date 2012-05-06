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
import scala.annotation.tailrec
import com.bluemarsh.graphmaker.core.util.FibonacciHeap
import FibonacciHeap.Node

/** Implements Dijkstra's algorithm on top of a fibonacci heap, O(V * log(V) + E). */
final class ShortestPaths(val graph: Graph, center: Vec) {
  private[this] val inf = Long.MaxValue
  private[this] val dist = Map[Vec, Long]().withDefaultValue(inf)
  private[this] val qNodes = Map[Vec, Node[Vec]]()
  private[this] val previous = Map[Vec, Vec]()
  
  dist.sizeHint(graph.areaSize * graph.areaSize)
  previous.sizeHint(graph.areaSize * graph.areaSize)
  
  dist.put(center, 0L)
  
  private[this] val q = new FibonacciHeap[Vec]
  
  graph.nodes foreach { vec =>
    val node = q insert (vec, inf)
    qNodes += (vec -> node)
  }
  
  qNodes get center foreach (n => q decreaseKey (n, 0))
  
  while (! q.isEmpty && dist.contains(q.min.data)) {
    val closest = q.removeMin
    val neighbours = graph neighboursOf closest.data
    
    qNodes -= closest.data
    
    while (neighbours hasNext) {
      val (neighbour, weight) = neighbours.next
      
      if (qNodes contains neighbour) {
        val newDistance = dist(closest.data) + weight 
      
        val dn = dist(neighbour)
        if ((dn != inf && newDistance < dn) || (dn == inf)) {
          dist put (neighbour, newDistance)
          previous put (neighbour, closest.data)
          q decreaseKey (qNodes(neighbour), newDistance)
        }
      }
    }
  }
  
  def distanceTo(v: Vec): Option[Long] = dist.get(v)
      
  @tailrec
  def firstStepTo(n: Vec): Option[Vec] =
    if (! previous.contains(n)) None
    else if (previous(n) == center) Some(n)
    else firstStepTo(previous(n))
    
  @tailrec
  def pathTo(n: Vec, p: List[Vec] = List()): List[Vec] =
    if (! previous.contains(n)) p
    else pathTo(previous(n), n :: p)
}
