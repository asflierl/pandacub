package eu.flierl

import scala.util.parsing.input.Reader
import scala.util.parsing.input.CharSequenceReader

package object pandacub {
  type State = (BotState, String)
  
  type =/>[-A, +B] = PartialFunction[A, B]
  
  def init[A](a: A)(f: A => Any) = { f(a); a }
  
  def ??? = throw new UnsupportedOperationException("not yet implemented")
}