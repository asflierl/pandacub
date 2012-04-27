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
  val react = (fromServer: String) => parseAll(opcode, fromServer) match {
    case Success(_, _) => 
      "Status(text=pondering...)"
    case f @ Failure(m, r) => 
      println(show(FailureDetail(f, fromServer)))
      "Status(text=uh oh)"
  } 
}