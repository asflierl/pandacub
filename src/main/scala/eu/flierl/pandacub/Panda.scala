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
  val react = (fromServer: String) => show(parseAll(opcode, fromServer) match {
    case Success(_, _) => 
      Status("whee") +: Log("muuh")
    case f @ Failure(_,_) => 
      println(show(FailureDetail(f, fromServer)))
      List(Status("oh noooo"))
  })
}