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
import Utils.viewFrom

@RunWith(classOf[JUnitRunner])
class ViewSpec extends Specification {
  "The view" should { 
                                                                                         
    "build a graph correctly" in {
      val graph = viewFrom(
        "_b__P"
      + "_Sp__"
      + "W_M_s"
      + "__W__"
      + "m???B").graph()
                     
      graph.nodes.map(_.value) must haveTheSameElementsAs (List(
        (0, 0), (2, 0), (3, 0), (4, 0),
        (0, 1), (1, 1), (3, 1), (4, 1),
        (1, 2), (2, 2), (3, 2),
        (0, 3), (1, 3), (3, 3), (4, 3),
        (1, 4), (2, 4), (3, 4), (4, 4)
      ) map Vec.tupled)
    }
    
    "connect graph nodes correctly" in {
      val graph = viewFrom(
        "___"          // 3 + 3 + 1
      + "_M_"          // 3 + 4 + 1
      + "W__").graph() // 0 + 2 + 0
        
      graph.nodes.map(_.value) must not contain(Vec(0, 2))
      
      graph.edges must have size(17)
    }
    
  }                           
}
