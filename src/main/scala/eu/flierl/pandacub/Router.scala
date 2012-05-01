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

import Grammar.{ 
  parseAll, 
  opcode, 
  Success, 
  Failure, 
  FailureDetail
}
import Show.show

object Router {
  private[this] object Monitor
  
  lazy val parseAndRoute: State =/> State = { 
    case (state, fromServer) =>
      try {
        Monitor synchronized unsafeParseAndRoute(state, fromServer)
      } catch {
        case exc: Exception =>
          exc.printStackTrace
          (state, show(Status("oh noooo")))
      } 
    }
  
  private def unsafeParseAndRoute(state: BotState, fromServer: String): State =
    parseAll(opcode, fromServer) match {
      case Success(op, _) =>
        decideRoute(op, state)
      case f @ Failure(_,_) => 
        println(show(FailureDetail(f, fromServer)))
        (state, show(Status("Wat?")))
    }
    
  private def decideRoute(op: OpcodeFromServer, state: BotState): State = op match {
    case Welcome(name, _, apocalypse, round) => 
      (state, show(Status("..zzzZZ")))
      
    case MasterReact(_, time, view, energy) =>
      new Panda(state).react(time, view, energy)
      
    case MiniReact(generation, name, time, view, energy, master) =>
      (state, "")
      
    case Goodbye(energy) =>
      (state, "")
  }
}
