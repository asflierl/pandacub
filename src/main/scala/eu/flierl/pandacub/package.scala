package eu.flierl

import scala.util.parsing.input.Reader
import scala.util.parsing.input.CharSequenceReader
import scalax.collection.Graph
import scalax.collection.edge.WUnDiEdge

package object pandacub {
  type State = (BotState, String)
  
  type =/>[-A, +B] = PartialFunction[A, B]
  
  type G = Graph[Vec, WUnDiEdge]
  
  type N = G#NodeT
  
  def init[A](a: A)(f: A => Any) = { f(a); a }
  
  def ??? = throw new UnsupportedOperationException("not yet implemented")
  
  def sqrt(n: Int): Int = math.sqrt(n).toInt
}