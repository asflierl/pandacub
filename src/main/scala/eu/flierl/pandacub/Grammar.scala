package eu.flierl.pandacub

import scala.util.parsing.combinator._
import java.io.File
import scala.util.parsing.input.CharSequenceReader
import scala.util.parsing.input.Reader
import Show.show

object Grammar extends JavaTokenParsers {
  lazy val opcode: Parser[OpcodeFromServer] = welcome | react | goodbye
  
  lazy val welcome: Parser[Welcome] = 
    "Welcome(" ~> repsep(
        (("name" | "path")        ~ '=' ~ string) 
      | (("apocalypse" | "round") ~ '=' ~ wholeNumber)
      , ','
    ) <~ ')' ^^ sortByKey ^? { 
      case List("apocalypse"~_~a, "name"~_~n, "path"~_~p, "round"~_~r) => 
        Welcome(n, new File(p), a toInt, r toInt)
    }
    
  lazy val react: Parser[React] =
    "React(" ~> repsep(reactAttribute, ',') <~ ')' ^^ filterOptionalReactAttributes ^^ sortByKey ^? {
      case List("energy"~_~e, "generation"~_~"0", "name"~_~n, "time"~_~t, "view"~_~v) =>
        MasterReact(0, n, t toInt, v, e toInt)
        
      case List("energy"~_~e, "generation"~_~g, "master"~_~m, "name"~_~n, "time"~_~t,
          "view"~_~v) if g.toInt > 0 =>
        MiniReact(g toInt, n, t toInt, v, e toInt, parseAll(vec, m).get)
    }
    
  lazy val reactAttribute: Parser[String ~ Char ~ String] = 
    (("name" | "view")                  ~ '=' ~ string)      | 
    (("generation" | "time" | "energy") ~ '=' ~ wholeNumber) |
    ("master"                           ~ '=' ~ vecStr)      |
    (string                             ~ '=' ~ string)
    
  lazy val goodbye: Parser[Goodbye] = 
    "Goodbye(energy=" ~> wholeNumber <~ ")" ^^ (_.toInt) ^^ Goodbye
  
  lazy val string: Parser[String] = "[^=(),|]*".r
  lazy val vecStr: Parser[String] = vec ^^ show[Vec]
  lazy val vec: Parser[Vec] = (wholeNumber ~ ':' ~ wholeNumber) ^? { case x ~ _ ~ y => Vec(x toInt, y toInt) }
  lazy val sortByKey = (l: List[String ~ Char ~ String]) => l.sortBy(_._1._1)
  
  lazy val filterOptionalReactAttributes = (l: List[String ~ Char ~ String]) => l.filter { x => 
    Set("name", "view", "generation", "time", "energy", "master") contains x._1._1
  }.toSet.toList
  
  case class FailureDetail(failure: Failure, input: String)
}