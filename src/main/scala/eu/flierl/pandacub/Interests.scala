package eu.flierl.pandacub

import Cells._

object Interests {
  sealed class Interest(val cell: Cell, val status: String, val prio: Int)

  case object InterestingFluppet extends Interest(Fluppet, "*hug*",     2)
  case object InterestingBamboo  extends Interest(Bamboo,  "*munch*",   1)
  case object InterestingFog     extends Interest(Fog,     "*explore*", 0)
  case object InterestingEmpty   extends Interest(Empty,   "*roam*",    0)
}
