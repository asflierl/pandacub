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

import scala.annotation.implicitNotFound
import Cells.Cell

trait Show[A] {
  def showSome(a: A): String
}
object Show {
  def show[A](thing: A)(implicit please: Show[A]): String = please showSome thing  
  
  implicit object FailureCanShow extends Show[Grammar.FailureDetail] {
    def showSome(d: Grammar.FailureDetail) = "%s%nat: %s%n%s^".format(d.failure.msg, d.input, 
      " " * (d.input.indexOf(d.failure.next) + 4))
  }

  implicit object IntCanShow extends Show[Int] {
    def showSome(i: Int) = Integer toString i
  }
  
  implicit def CharSequenceCanShow[A <: CharSequence]: Show[A] = new Show[A] {
    def showSome(c: A) = c toString
  }
  
  implicit object VecCanShow extends Show[Vec] {
    def showSome(v: Vec) = "%d:%d" format (v.x, v.y)
  }
  
  implicit object MoveCanShow extends Show[Move] {
    def showSome(e: Move) = "Move(%s)" format keyValue("direction", show(e.direction))
  }
  
  implicit object SpawnCanShow extends Show[Spawn] {
    def showSome(s: Spawn) = "Spawn(%s,%s,%s)" format (
      keyValue("direction", show(s.direction)),
      keyValue("name", show(s.name)),
      keyValue("energy", show(s.energy)))
  }
  
  implicit object ExplodeCanShow extends Show[Explode] {
    def showSome(e: Explode) = "Explode(%s)" format keyValue("size", show(e.size))
  }
  
  implicit object StatusCanShow extends Show[Status] {
    def showSome(s: Status) = "Status(%s)" format keyValue("text", s.text)
  }
  
  implicit object LogCanShow extends Show[Log] {
    def showSome(l: Log) = "Log(%s)" format keyValue("text", l.text)
  }
  
  implicit object SayCanShow extends Show[Say] {
    def showSome(s: Say) = "Say(%s)" format keyValue("text", s.text)
  }
  
  implicit object CellCanShow extends Show[Cell] {
    def showSome(c: Cell) = c.symbol.toString
  } 
  
  implicit object ViewCanShow extends Show[View] {
    def showSome(v: View) = (for {
      y <- (0 until v.len)
      x <- (0 until v.len)
      cell = v area Vec(x, y)
    } yield show(cell)) mkString
  }
  
  implicit object OpcodeFromBotCanShow extends Show[OpcodeFromBot] {
    def showSome(op: OpcodeFromBot) = op match {
      case s: Spawn => show(s)
      case e: Explode => show(e)
      case s: Status => show(s)
      case l: Log => show(l)
      case s: Say => show(s)
      case m: Move => show(m)
    }
  }
  
  implicit def OpcodeListCanShow[A <: OpcodeFromBot]: Show[List[A]] = new Show[List[A]] {
    def showSome(l: List[A]) = l map show[OpcodeFromBot] mkString "|"
  }
  
  def keyValue[A: Show, B: Show](key: A, value: B): String = "%s=%s" format (
    sanitize(show(key)), sanitize(show(value)))
  
  def sanitize(str: String): String = str.replaceAll("[=(),|]*", "") 
}
