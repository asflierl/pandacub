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
  lazy val parseAndRoute: State =/> State = { 
    case (state, fromServer) =>
      try {
        unsafeParseAndRoute(state, fromServer)
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
      val move = Status("moving") +: Move(Vec(1, 0)) +: Spawn(Vec(-1, 0), "child" + time, 100)
      (state, show(if (time == 0) Say("Whee!") :: move else move))
      
    case MiniReact(generation, name, time, view, energy, master) =>
      (state, "")
      
    case Goodbye(energy) =>
      (state, "")
  }
}