package eu.flierl.pandacub

import Cells._
import Show.show

final class Panda(state: BotState) {
  def react(time: Int, view: View, energy: Int): State = {
    val center = Vec(view.len / 2, view.len / 2)
//    def node(n: Vec): view.graph.NodeT = view.graph get n
    val paths = new ShortestPaths(view.graph, center)
    
    println("\n")
    println(view.toString)
    println("\n")
    
    val food = for {
      f <- (view.all(Bamboo) ++ view.all(Fluppet)).toSeq
      d <- paths.distanceTo(f)
    } yield (d, f)

    if (! food.isEmpty) {
      val nextFood = food.minBy(_._1)._2
      val target = paths.firstStepToVec(nextFood).get.value
      (state, show(Move(target - center) +: Status("*munch*")))
    } else {
      (state, show(Status("nowhere to go")))
    }
//    
//    if (food isEmpty) {
//      val fogs = view.all(Fog).toSeq.flatMap(n => pathTo(n)).sortBy(_.length)
//      if (! fogs.isEmpty) {
//        val nextFog = fogs.head.nodes.tail.head
//        (state, show(Move(nextFog - center) +: Status("going to fog")))
//      } else {
//        val empties = view.all(Empty).toSeq.flatMap(n => pathTo(n)).sortBy(_.length).reverse
//        if (! empties.isEmpty) {
//          val next = fogs.head.nodes.tail.head
//          val x = fogs.headOption.map(_.nodes)
//          (state, show(Move(next - center) +: Status("going to empty")))
//        } else {
//          (state, show(Status("nowhere to go")))
//        }
//      }
//    } else {
//      val pathToNext = food.sortBy(_.length).head
//      (state, show(Move(pathToNext.nodes.tail.head - center) +: Status("*munch*")))
//    }
  }  
}