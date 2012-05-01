package eu.flierl.pandacub

import scala.annotation.implicitNotFound
import Cells.Cell

trait Show[A] {
  def showSome(a: A): String
}
object Show {
  def show[A](thing: A)(implicit please: Show[A]): String = please showSome thing  
  
  implicit object FailureCanShow extends Show[Grammar.FailureDetail] {
    def showSome(d: Grammar.FailureDetail) = "%s%nat: %s%n%s^".format(d.failure.msg, d.input, 
      " " * (d.input.indexOf(d.failure.next) + 4))
  }

  implicit object IntCanShow extends Show[Int] {
    def showSome(i: Int) = Integer toString i
  }
  
  implicit def CharSequenceCanShow[A <: CharSequence]: Show[A] = new Show[A] {
    def showSome(c: A) = c toString
  }
  
  implicit object VecCanShow extends Show[Vec] {
    def showSome(v: Vec) = "%d:%d" format (v.x, v.y)
  }
  
  implicit object MoveCanShow extends Show[Move] {
    def showSome(e: Move) = "Move(%s)" format keyValue("direction", show(e.direction))
  }
  
  implicit object SpawnCanShow extends Show[Spawn] {
    def showSome(s: Spawn) = "Spawn(%s,%s,%s)" format (
      keyValue("direction", show(s.direction)),
      keyValue("name", show(s.name)),
      keyValue("energy", show(s.energy)))
  }
  
  implicit object ExplodeCanShow extends Show[Explode] {
    def showSome(e: Explode) = "Explode(%s)" format keyValue("size", show(e.size))
  }
  
  implicit object StatusCanShow extends Show[Status] {
    def showSome(s: Status) = "Status(%s)" format keyValue("text", s.text)
  }
  
  implicit object LogCanShow extends Show[Log] {
    def showSome(l: Log) = "Log(%s)" format keyValue("text", l.text)
  }
  
  implicit object SayCanShow extends Show[Say] {
    def showSome(s: Say) = "Say(%s)" format keyValue("text", s.text)
  }
  
  implicit object CellCanShow extends Show[Cell] {
    def showSome(c: Cell) = c.symbol.toString
  } 
  
  implicit object ViewCanShow extends Show[View] {
    def showSome(v: View) = (for {
      y <- (0 until v.len)
      x <- (0 until v.len)
      cell = v area Vec(x, y)
    } yield show(cell)) mkString
  }
  
  implicit object OpcodeFromBotCanShow extends Show[OpcodeFromBot] {
    def showSome(op: OpcodeFromBot) = op match {
      case s: Spawn => show(s)
      case e: Explode => show(e)
      case s: Status => show(s)
      case l: Log => show(l)
      case s: Say => show(s)
      case m: Move => show(m)
    }
  }
  
  implicit def OpcodeListCanShow[A <: OpcodeFromBot]: Show[List[A]] = new Show[List[A]] {
    def showSome(l: List[A]) = l map show[OpcodeFromBot] mkString "|"
  }
  
  def keyValue[A: Show, B: Show](key: A, value: B): String = "%s=%s" format (
    sanitize(show(key)), sanitize(show(value)))
  
  def sanitize(str: String): String = str.replaceAll("[=(),|]*", "") 
}