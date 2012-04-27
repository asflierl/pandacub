package eu.flierl.pandacub

import Grammar.{ 
  parseAll, 
  opcode, 
  Success, 
  Failure, 
  FailureDetail
}
import Show.show

class Panda {
  val react: State =/> State = { 
    case (state, fromServer) => 
      parseAll(opcode, fromServer) match {
        case Success(op, _) =>
          reactTo(op, state)
        case f @ Failure(_,_) => 
          println(show(FailureDetail(f, fromServer)))
          (state, show(Status("oh noooo")))
      }
    }
  
  def reactTo(op: OpcodeFromServer, state: BotState): State = op match {
    case Welcome(name, _, apocalypse, round) => 
      (state, show(Status("..zzzZZ")))
      
    case MasterReact(generation, name, time, view, energy) =>
      println(view.length)
      val move = Status("moving") +: Move(Vec(1, 0))
      (state, show(if (time == 0) Say("Whee!") :: move else move))
      
    case MiniReact(generation, name, time, view, energy, master) =>
      (state, "")
      
    case Goodbye(energy) =>
      (state, "")
  }
}