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

import scala.util.parsing.combinator._
import java.io.File
import scala.util.parsing.input.CharSequenceReader
import scala.util.parsing.input.Reader
import Show.show
import Cells._

object Grammar extends JavaTokenParsers {
  lazy val opcode: Parser[OpcodeFromServer] = welcome | react | goodbye
  
  lazy val welcome: Parser[Welcome] = 
    "Welcome(" ~> repsep(
        (("name" | "path")        ~ '=' ~ string) 
      | (("apocalypse" | "round") ~ '=' ~ wholeNumber)
      , ','
    ) <~ ')' ^^ sortByKey ^? { 
      case List("apocalypse"~_~a, "name"~_~n, "path"~_~p, "round"~_~r) => 
        Welcome(n, new File(p), a toInt, r toInt)
    }
    
  lazy val react: Parser[React] =
    "React(" ~> repsep(reactAttribute, ',') <~ ')' ^^ filterOptionalReactAttributes ^^ sortByKey ^? {
      case List("energy"~_~e, "generation"~_~"0", "name"~_~n, "time"~_~t, "view"~_~v) =>
        MasterReact(n, t toInt, parseAll(view, v) get, e toInt)
        
      case List("energy"~_~e, "generation"~_~g, "master"~_~m, "name"~_~n, "time"~_~t,
          "view"~_~v) if g.toInt > 0 =>
        MiniReact(g toInt, n, t toInt, parseAll(view, v) get, e toInt, parseAll(vec, m) get)
    }
    
  lazy val reactAttribute: Parser[String ~ Char ~ String] = 
    ("name"                             ~ '=' ~ string)      |
    ("view"                             ~ '=' ~ viewString)  |
    (("generation" | "time" | "energy") ~ '=' ~ wholeNumber) |
    ("master"                           ~ '=' ~ vecStr)      |
    (string                             ~ '=' ~ string)
    
  lazy val goodbye: Parser[Goodbye] = 
    "Goodbye(energy=" ~> wholeNumber <~ ")" ^^ (_.toInt) ^^ Goodbye
  
  lazy val string: Parser[String] = "[^=(),|]*".r
  lazy val vecStr: Parser[String] = vec ^^ show[Vec]
  lazy val vec: Parser[Vec] = (wholeNumber ~ ':' ~ wholeNumber) ^? { case x ~ _ ~ y => Vec(x toInt, y toInt) }
  lazy val sortByKey = (l: List[String ~ Char ~ String]) => l.sortBy(_._1._1)
  
  lazy val filterOptionalReactAttributes = (l: List[String ~ Char ~ String]) => l.filter { x => 
    Set("name", "view", "generation", "time", "energy", "master") contains x._1._1
  }.toSet.toList
  
  lazy val viewString: Parser[String] = view ^^ show[View]
  lazy val view: Parser[View] = rep1(cell) ^^ View
  lazy val cell: Parser[Cell] = allCells map (c => elem(c.symbol) ^^ (_ => c)) reduceLeft(_ | _)
  
  case class FailureDetail(failure: Failure, input: String)
}
