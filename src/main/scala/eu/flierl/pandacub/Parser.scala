package eu.flierl.pandacub

import scala.collection.mutable.Map
import java.io.File
import Cells.allCells

object Parser {
  val opcode = """(\w+)\((.+)\)""".r.pattern
  val keys = """([^=(),|]+)=([^=(),|]*)""".r.pattern
  val vecs = """(-?\d+):(-?\d+)""".r.pattern
  val cellMap = allCells map (c => c.symbol -> c) toMap
  
  def parse(s: String): OpcodeFromServer = {
    val opcodeMatcher = opcode.matcher(s)
    opcodeMatcher.find
    val op = opcodeMatcher.group(1)
    val data = opcodeMatcher.group(2)
    
    val kvMatcher = keys.matcher(data)
    val pairs = Map[String, String]()
    
    while (kvMatcher find) {
      pairs put (kvMatcher group 1, kvMatcher group 2)
    }

    if (op == "Welcome")
      Welcome(
        pairs("name"), 
        new File(pairs("path")),
        pairs("apocalypse") toInt,
        pairs("round") toInt)
    else if (op == "React")
      if (pairs contains "master")
        MiniReact(
          pairs("generation") toInt,
          pairs("name"),
          pairs("time") toInt,
          view(pairs("view")),
          pairs("energy") toInt,
          vec(pairs("master")))
      else 
        MasterReact(
          pairs("name"),
          pairs("time") toInt,
          view(pairs("view")),
          pairs("energy") toInt)
    else 
      Goodbye(pairs("energy").toInt)
  }
  
  def vec(s: String): Vec = {
    val m = vecs matcher s
    m.find
    Vec(m group 1 toInt, m group 2 toInt)
  }
  
  def view(s: String): View = {
    require((s length) % 2 == 1, "there must be an odd number of cells")
    val n = sqrt(s length)
    
    View(n, (s.view map cellMap zipWithIndex) map { case (c, i) => Vec(i % n, i / n) -> c } toMap)
  }
}