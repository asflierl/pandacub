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
        case Success(_, _) =>
          (new BotState, show(List(Status("whee: "))))
        case f @ Failure(_,_) => 
          println(show(FailureDetail(f, fromServer)))
          (new BotState, show(List(Status("oh noooo"))))
      }
    }
}