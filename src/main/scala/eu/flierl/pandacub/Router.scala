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

import Show.show

object Router {
  private[this] val localGrammar = new ThreadLocal[Grammar]()  
  
  lazy val parseAndRoute: OpWithGlobalState =/> OpWithGlobalUpdate = { 
    case (state, fromServer) =>
      try {
        val g = grammar
        val parsed = g.parseAll(g.opcode, fromServer) 
        unsafeParseAndRoute(state, g, parsed, fromServer)
      } catch {
        case exc: Exception =>
          exc.printStackTrace
          (identity, show(Status("oh noooo")))
      } 
    }
  
  private[this] def grammar = {
    if (localGrammar.get == null) {
      localGrammar set (new Grammar)
    }
    localGrammar.get
  }
  
  private[this] def unsafeParseAndRoute(state: GlobalState, grammar: Grammar, 
      result: Grammar#ParseResult[OpcodeFromServer], fromServer: String): OpWithGlobalUpdate =
    result match {
      case grammar.Success(op, _) =>
        decideRoute(op, state)
      case f @ grammar.Failure(_,_) => 
        println(show[Grammar#FailureDetail](grammar.FailureDetail(f, fromServer)))
        (identity, show(Status("Wat?")))
    }
    
  private[this] def decideRoute(op: OpcodeFromServer, state: GlobalState): OpWithGlobalUpdate = op match {
    case Welcome(name, dir, apocalypse, round) =>
      (_ copy (apocalypse = apocalypse), show(Status("..zzzZZ")))
      
    case MasterReact(name, time, view, energy) =>
      socialize(name, new Panda(state botStates name, state apocalypse).react(time, view, energy))
      
    case MiniReact(generation, name, time, view, energy, master) =>
      socialize(name, new Cub(state botStates name, state apocalypse).react(time, view, energy, master))
      
    case Goodbye(energy) =>
      EndOfRound(state, energy)
  }
  
  private[this] def socialize(name: String, op: OpWithState): OpWithGlobalUpdate = op match {
    case (state, op) => 
      (g => (g.copy(botStates = g.botStates + (name -> state))), op)
  }
}
