package eu.flierl.pandacub

trait Show[A] {
  def show(a: A): String
}
object Show {
  implicit val VecCanShow = new Show[Vec] {
    def show(v: Vec) = "%d:%d" format (v.x, v.y)
  }
}