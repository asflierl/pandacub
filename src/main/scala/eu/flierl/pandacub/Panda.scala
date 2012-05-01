package eu.flierl.pandacub

import Cells._
import Show.show

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._

final class Panda(state: BotState) {
  def react(time: Int, view: View, energy: Int): State = decideBasedOn(state, view) nextMove
  
  val decideBasedOn = new PointsOfInterest(_: BotState, _: View) with MovementDecision {
    def nextMove =
      closest (Bamboo,  "*munch*")       orElse (
      closest (Fluppet, "*hug*"))        orElse (
      farthest(Fog,     "*explore*"))    orElse (
      farthest(Empty,   "*roam*"))    getOrElse (
      confused)
  }  
}
