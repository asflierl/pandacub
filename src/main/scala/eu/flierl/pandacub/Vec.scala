package eu.flierl.pandacub

case class Vec(x: Int, y: Int) {
  def +(v: Vec) = Vec(x + v.x, y + v.y)
  def -(v: Vec) = Vec(x - v.x, y - v.y)
}

object Vec extends ((Int, Int) => Vec) {
  implicit val DefaultOrdering: Ordering[Vec] = new Ordering[Vec] {
    def compare(a: Vec, b: Vec) = {
      val priorityCriterion = implicitly[Ordering[Int]].compare(a.x, b.x) 
      if (priorityCriterion != 0) priorityCriterion
      else implicitly[Ordering[Int]].compare(a.y, b.y)
    }
  }
}