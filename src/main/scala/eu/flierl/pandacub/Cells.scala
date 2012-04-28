package eu.flierl.pandacub

object Cells {
  sealed class Cell(val symbol: Char)
  
  case object Fog extends Cell('?')
  case object Empty extends Cell('_')
  case object Wall extends Cell('W')
  case object Panda extends Cell('M')
  case object Tiger extends Cell('m')
  case object Cub extends Cell('S')
  case object Kitty extends Cell('s')
  case object Bamboo extends Cell('P')
  case object Shroom extends Cell('p')
  case object Fluppet extends Cell('B')
  case object Snorg extends Cell('b')
  
  lazy val allCells = Set[Cell](Fog, Empty, Wall, Panda, Tiger, Cub,
    Kitty, Bamboo, Shroom, Fluppet, Snorg)
}
