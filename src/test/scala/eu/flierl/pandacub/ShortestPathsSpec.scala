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

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.WUnDiEdge
import Utils.viewFrom

@RunWith(classOf[JUnitRunner])
class ShortestPathsSpec extends Specification {
  "The Dijkstra shortest path search algorithm" should { 
                                                                                         
    "find the only path available" in {
      val graph: G = Graph(Vec(0, 0) ~ Vec(1, 0) % 1L)
      val paths = new ShortestPaths(graph, Vec(0, 0))
      
      paths.distanceTo(Vec(1, 0)) must be some 1L
    }
    
    "find the lighter one of 2 available paths" in {
      val graph: G = Graph(
        Vec(0, 0) ~ Vec(1, 0) % 2L,
        Vec(1, 0) ~ Vec(1, 1) % 2L,
        
        Vec(0, 0) ~ Vec(0, 1) % 1L,
        Vec(0, 1) ~ Vec(1, 1) % 1L)
        
      val paths = new ShortestPaths(graph, Vec(0, 0))
      
      paths.distanceTo(graph get Vec(1, 1)) must be some 2L
    }
    
    "find the shorter one of 2 available paths" in {
      val graph: G = Graph(
        Vec(0, 0) ~ Vec(1, 0) % 1L,
        Vec(1, 0) ~ Vec(2, 0) % 1L,
        Vec(2, 0) ~ Vec(1, 1) % 1L,
        
        Vec(0, 0) ~ Vec(0, 1) % 1L,
        Vec(0, 1) ~ Vec(1, 1) % 1L)
        
      val paths = new ShortestPaths(graph, Vec(0, 0))
      
      paths.distanceTo(Vec(1, 1)) must be some 2L
    }
    
    "find some bamboo" in {
      val graph = viewFrom(
        "_______"
      + "______P"
      + "_______"
      + "___M___"
      + "_______"
      + "_______"
      + "_______").graph()
        
      val paths = new ShortestPaths(graph, Vec(3, 3))
      
      paths.distanceTo(Vec(6, 1)) must be some
    }
    
    "find around an obstacle" in {
      val graph = viewFrom(
        "_________"
      + "_WWWWW___"
      + "_____W___"
      + "_____W___"
      + "____MW___"
      + "_____W_P_"
      + "_____W___"
      + "___WWW___"
      + "_________").graph()
        
      val paths = new ShortestPaths(graph, Vec(4, 4))
      
      paths.distanceTo(Vec(7, 5)) must be some 9
      
      paths.pathTo(Vec(7, 5)) must be equalTo List(
        Vec(3, 5), Vec(2, 6), Vec(2, 7), Vec(3, 8), Vec(4, 8), 
        Vec(5, 8), Vec(6, 7), Vec(6, 6), Vec(7, 5))
      
      paths.firstStepTo(Vec(7, 5)) must be some Vec(3, 5)
    }
  }                           
}
