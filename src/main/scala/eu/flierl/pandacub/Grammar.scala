package eu.flierl.pandacub

import scala.util.parsing.combinator._
import java.io.File
import scala.util.parsing.input.CharSequenceReader
import scala.util.parsing.input.Reader

object Grammar extends JavaTokenParsers {
  lazy val string: Parser[String] = "[^=(),|]*".r
  lazy val vecStr: Parser[String] = vec ^^ (_.toString)
  lazy val vec: Parser[Vec] = (wholeNumber ~ ':' ~ wholeNumber) ^? { case x ~ _ ~ y => Vec(x toInt, y toInt) }
  
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
    "React(" ~> repsep(
        (("entity" | "view")                ~ '=' ~ string) 
      | (("generation" | "time" | "energy") ~ '=' ~ wholeNumber)
      | ("master"                           ~ '=' ~ vecStr)
      , ','
    ) <~ ')' ^^ sortByKey ^? {
      case List("energy"~_~nrg, "entity"~_~ntt, "generation"~_~"0", "time"~_~t, "view"~_~v) =>
        MasterReact(0, ntt, t toInt, v, nrg toInt)
        
      case List("energy"~_~nrg, "entity"~_~ntt, "generation"~_~g, "master"~_~m, "time"~_~t, "view"~_~v) =>
        MiniReact(g toInt, ntt, t toInt, v, nrg toInt, parseAll(vec, m).get)
    }
    
  lazy val goodbye: Parser[Goodbye] = 
    "Goodbye(energy=" ~> wholeNumber <~ ")" ^^ (_.toInt) ^^ Goodbye
  
  lazy val sortByKey = (l: List[String ~ Char ~ String]) => l.sortBy(_._1._1)  
}