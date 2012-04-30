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
      
      paths.distanceTo(Vec(7, 5)) must be some
    }
  }                           
}