package eu.flierl.pandacub

trait MovementDecision {
  def nextMove: State
}