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

import scala.collection.mutable.Map
import java.io.File
import Cells.allCells

object Parser {
  val opcode = """(\w+)\((.+)\)""".r.pattern
  val keys = """([^=(),|]+)=([^=(),|]*)""".r.pattern
  val vecs = """(-?\d+):(-?\d+)""".r.pattern
  val cellMap = allCells map (c => c.symbol -> c) toMap
  
  def parse(s: String): OpcodeFromServer = {
    val opcodeMatcher = opcode.matcher(s)
    opcodeMatcher.find
    val op = opcodeMatcher.group(1)
    val data = opcodeMatcher.group(2)
    
    val kvMatcher = keys.matcher(data)
    val pairs = Map[String, String]()
    
    while (kvMatcher find) {
      pairs put (kvMatcher group 1, kvMatcher group 2)
    }

    if (op == "Welcome")
      Welcome(
        pairs("name"), 
        new File(pairs("path")),
        pairs("apocalypse") toInt,
        pairs("round") toInt)
    else if (op == "React")
      if (pairs contains "master")
        MiniReact(
          pairs("generation") toInt,
          pairs("name"),
          pairs("time") toInt,
          view(pairs("view")),
          pairs("energy") toInt,
          vec(pairs("master")))
      else 
        MasterReact(
          pairs("name"),
          pairs("time") toInt,
          view(pairs("view")),
          pairs("energy") toInt)
    else 
      Goodbye(pairs("energy").toInt)
  }
  
  def vec(s: String): Vec = {
    val m = vecs matcher s
    m.find
    Vec(m group 1 toInt, m group 2 toInt)
  }
  
  def view(s: String): View = {
    require((s length) % 2 == 1, "there must be an odd number of cells")
    val n = sqrt(s length)
    
    View(n, (s.view map cellMap zipWithIndex) map { case (c, i) => Vec(i % n, i / n) -> c } toMap)
  }
}