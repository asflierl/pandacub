package eu.flierl.pandacub

trait Show[A] {
  def show(a: A): String
}
object Show {
  def show[A](something: A)(implicit plz: Show[A]): String = plz show something  
  
  implicit val FailureCanShow = new Show[Grammar.FailureDetail] {
    def show(d: Grammar.FailureDetail) = "%s%nat: %s%n%s^".format(d.failure.msg, d.input, 
      " " * (d.input.indexOf(d.failure.next) + 4))
  }
  
  implicit val VecCanShow = new Show[Vec] {
    def show(v: Vec) = "%d:%d" format (v.x, v.y)
  }
}