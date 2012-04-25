package eu.flierl.pandacub

import scala.util.parsing.combinator._

object Grammar extends JavaTokenParsers {
  val string: Parser[String] = "[^=(),|]*".r
  
  val welcome: Parser[Welcome] = 
    "Welcome(" ~> repsep(
        ("name"       ~ '=' ~ string) 
      | ("path"       ~ '=' ~ string)
      | ("apocalypse" ~ '=' ~ wholeNumber)
      | ("round"      ~ '=' ~ wholeNumber) 
      , ','
    ) <~ ')' ^^ (_.sortBy { case k~_~v => k }) ^? { 
      case List("apocalypse"~'='~a, "name"~'='~n, "path"~'='~p, "round"~'='~r) => 
        Welcome(n, p, a.toInt, r.toInt)
    }
}