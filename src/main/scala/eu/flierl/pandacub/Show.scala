package eu.flierl.pandacub

import scala.annotation.implicitNotFound

trait Show[A] {
  def showSome(a: A): String
}
object Show {
  def show[A](thing: A)(implicit please: Show[A]): String = please showSome thing  
  
  implicit val FailureCanShow: Show[Grammar.FailureDetail] = new Show[Grammar.FailureDetail] {
    def showSome(d: Grammar.FailureDetail) = "%s%nat: %s%n%s^".format(d.failure.msg, d.input, 
      " " * (d.input.indexOf(d.failure.next) + 4))
  }

  implicit val IntCanShow: Show[Int] = new Show[Int] {
    def showSome(i: Int) = Integer toString i
  }
  
  implicit def CharSequenceCanShow[A <: CharSequence]: Show[A] = new Show[A] {
    def showSome(c: A) = c toString
  }
  
  implicit val OpcodeFromBotCanShow: Show[OpcodeFromBot] = new Show[OpcodeFromBot] {
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
  
  implicit val MoveCanShow: Show[Move] = new Show[Move] {
    def showSome(e: Move) = "Move(%s)" format keyValue("direction", show(e.direction))
  }
  
  implicit val SpawnCanShow: Show[Spawn] = new Show[Spawn] {
    def showSome(s: Spawn) = "Spawn(%s,%s,%s)" format (
      keyValue("direction", show(s.direction)),
      keyValue("name", show(s.name)),
      keyValue("energy", show(s.energy)))
  }
  
  implicit val ExplodeCanShow: Show[Explode] = new Show[Explode] {
    def showSome(e: Explode) = "Explode(%s)" format keyValue("size", show(e.size))
  }
  
  implicit val StatusCanShow: Show[Status] = new Show[Status] {
    def showSome(s: Status) = "Status(%s)" format keyValue("text", s.text)
  }
  
  implicit val LogCanShow: Show[Log] = new Show[Log] {
    def showSome(l: Log) = "Log(%s)" format keyValue("text", l.text)
  }
  
  implicit val SayCanShow: Show[Say] = new Show[Say] {
    def showSome(s: Say) = "Say(%s)" format keyValue("text", s.text)
  }
  
  implicit val VecCanShow: Show[Vec] = new Show[Vec] {
    def showSome(v: Vec) = "%d:%d" format (v.x, v.y)
  }
  
  def keyValue[A: Show, B: Show](key: A, value: B): String = "%s=%s" format (
    sanitize(show(key)), sanitize(show(value)))
  
  def sanitize(str: String): String = str.replaceAll("[=(),|]*", "") 
}