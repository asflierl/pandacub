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
import org.specs2.matcher.ParserMatchers

import java.io.File
import Cells._

@RunWith(classOf[JUnitRunner])
class GrammarSpec extends Specification with ParserMatchers {
  implicit val parsers = new Grammar
  
  import parsers.{ string, vec, welcome, react }
  
  "The opcode grammar" should { 
                                                                                         
    "parse property strings correctly" in {                                                   
      string must succeedOn("abc").withResult("abc")                                   
      string("ab,c") must beAFailure
    }
    
    "parse vectors correctly" in {                                                   
      vec must succeedOn("1:2").withResult(Vec(1, 2))
      vec must succeedOn("23:2").withResult(Vec(23, 2))
      vec must succeedOn("1:42").withResult(Vec(1, 42))
      vec must succeedOn("-1:2").withResult(Vec(-1, 2))
      vec must succeedOn("1:-2").withResult(Vec(1, -2))
      vec("x:1") must beAFailure
      vec("1:x") must beAFailure
      vec(":1") must beAFailure
      vec("1:") must beAFailure
    }
    
    "parse the 'Welcome' opcode correctly" in {
      welcome must succeedOn(
        "Welcome(name=String,path=string,apocalypse=42,round=23)"
      ).withResult(
        Welcome("String", new File("string"), 42, 23))
    }
    
    "parse the 'React' opcode correctly" in {
      react must succeedOn(
        "React(generation=0,time=23,view=WPs_MSBm?,name=pandacub,energy=-5)"
      ).withResult(
        MasterReact("pandacub", 23, exampleView, -5))
      
      react("React(generation=0,time=2,master=1:2,view=s,name=p,energy=0)") must beAFailure
        
      react("React(generation=1,time=2,view=s,name=p,energy=0)") must beAFailure
        
      react must succeedOn(
        "React(master=-8:12345,view=WPs_MSBm?,name=pandacub,generation=42,time=23,energy=-5)"
      ).withResult(
        MiniReact(42, "pandacub", 23, exampleView, -5, Vec(-8, 12345)))
    } 
  }
  
  def exampleView = View(3, Map(
    Vec(0, 0) -> Wall,
    Vec(1, 0) -> Bamboo,
    Vec(2, 0) -> Kitty,
    Vec(0, 1) -> Empty,
    Vec(1, 1) -> Panda,
    Vec(2, 1) -> Cub,
    Vec(0, 2) -> Fluppet,
    Vec(1, 2) -> Tiger,
    Vec(2, 2) -> Fog))
}
