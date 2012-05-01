/*
 * Copyright (c) 2012 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.flierl.pandacub

import Cells._
import Show.show
import collection.breakOut

abstract class PointsOfInterest(state: BotState, view: View) {
  private val center = Vec(view.len / 2, view.len / 2)
  private val paths = new ShortestPaths(view.graph(state.trailMap), center)
  
  def closest(cell: Cell, status: String, prio: Int) = best(cell, status, prio, _.minBy(_._1)) 
  
  def farthest(cell: Cell, status: String, prio: Int) = best(cell, status, prio, _.maxBy(_._1)) 
  
  val confused = (state, show(Status("*confused*")))
  
  type Ω = (Long, Vec) // distance and target
  
  def best(cell: Cell, status: String, prio: Int, select: Seq[Ω] => Ω): Option[State] = {
    val cells = pathsTo(cell)
    if (cells isEmpty) None
    else move(select, status, cells, prio)
  }
  
  private def pathsTo(cell: Cell): Seq[Ω] = 
    (for {
      f <- view all cell
      d <- paths distanceTo f
    } yield (d, f))(breakOut)
  
  private def move(select: Seq[Ω] => Ω, status: String, cells: Seq[Ω], prio: Int): Some[State] = {
    val (nextStep, nextFocus) = findNextStepAndFocus(select, cells, prio)
   
    Some((state.copy(trail = translatedAndFadedTrail(nextStep), lastFocus = Some(nextFocus)), 
          show(Move(nextStep - center) +: Status(status))))
  }
  
  private def findNextStepAndFocus(select: Seq[Ω] => Ω, cells: Seq[Ω], prio: Int): (Vec, Focus) = {
    val (distance, target) = select(cells)
    val discoveredFocus = Focus(target, view area target, prio)
    
    val focus = previousOrDiscoveredFocus(prio, distance, discoveredFocus)
    
    val nextStep = paths.firstStepToVec(focus.vec).get.value
    val nextFocus = focus copy (vec = translate(focus vec, nextStep))
    
    (nextStep, nextFocus)
  }
  
  private def previousOrDiscoveredFocus(prio: Int, distance: Long, discoveredFocus: Focus): Focus =
    (for {
      f @ Focus(v, c, p) <- state.lastFocus    if p >= prio
      d                  <- paths distanceTo v if d > 0
      currentContent      = view area v
      stillValid          = currentContent == c || currentContent == Fog
    } yield {
      if (p == prio) if (d < distance && stillValid) f else discoveredFocus
      else if (stillValid) f
      else discoveredFocus
    }) getOrElse discoveredFocus
  
  
  private def translatedAndFadedTrail(nextStep: Vec): List[Trail] = {
    val first = Trail(center, (view.len * 3L) / 2L + 2L)
    val nextTrail = first :: state.trail.takeWhile(_.discouragement > 0)
    
    nextTrail map { t =>
      t.copy(vec = translate(t.vec, nextStep), discouragement = (t.discouragement * 2L) / 3L)
    } 
  }
  
  private def translate(v: Vec, t: Vec) = v + center - t
}
