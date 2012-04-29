package eu.flierl.pandacub

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.WUnDiEdge

@RunWith(classOf[JUnitRunner])
class ShortestPathsSpec extends Specification {
  "The Dijkstra shortest path search algorithm" should { 
                                                                                         
    "find the only path available" in {
      val graph: G = Graph(Vec(0, 0) ~ Vec(1, 0) % 1L)
      val paths = new ShortestPaths(graph, graph get Vec(0, 0))
      
      paths.distanceTo(graph get Vec(1, 0)) must be some 1L
    }
    
    "find the lighter one of 2 available paths" in {
      val graph: G = Graph(
        Vec(0, 0) ~ Vec(1, 0) % 2L,
        Vec(1, 0) ~ Vec(1, 1) % 2L,
        Vec(0, 0) ~ Vec(0, 1) % 1L,
        Vec(0, 1) ~ Vec(1, 1) % 1L)
      val paths = new ShortestPaths(graph, graph get Vec(0, 0))
      
      println(paths.distances)
      
      paths.distanceTo(graph get Vec(1, 1)) must be some 2L
    }
  }                           
}