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
import Interests._
import Show.show
import collection.breakOut

abstract class PointsOfInterest(state: BotState, view: View) {
  private[this] val center = Vec(view.len / 2, view.len / 2)
  private[this] val paths = new ShortestPaths(view graph discouragements, center)
  private[this] def discouragements = state.trailMap ++ enemies
  private[this] def enemies = (view all Snorg).view flatMap view.neighbours map (_ -> 5L)
  
  def closest(interests: Interest*) = best(interests, _.minBy(_._1)) 
  
  def farthest(interests: Interest*) = best(interests, _.maxBy(_._1)) 
  
  val confused = (state, show(Status("*confused*")))
  
  type Ω = (Long, Vec, Interest)
  
  def best(interests: Seq[Interest], selectFrom: Seq[Ω] => Ω): Option[State] =
    for {
      desires <- Option(interests flatMap pathsTo) if ! desires.isEmpty
      desire = selectFrom(desires)
      nextState <- move(desire)
    } yield nextState
  
  private def pathsTo(interest: Interest): Seq[Ω] = 
    (for {
      f <- view all interest.cell
      d <- paths distanceTo f
    } yield (d, f, interest))(breakOut)
  
  private def move(desire: Ω): Some[State] = {
    val (distance, target, interest) = desire
    val (nextStep, nextFocus) = findNextStepAndFocus(distance, target, interest)
   
    Some((state.copy(trail = translatedAndFadedTrail(nextStep), lastFocus = Some(nextFocus)), 
          show(Move(nextStep - center) +: Status(interest.status))))
  }
  
  private[this] def findNextStepAndFocus(distance: Long, target: Vec, interest: Interest): (Vec, Focus) = {
    val discoveredFocus = Focus(target, interest cell, interest prio)
    
    val focus = previousOrDiscoveredFocus(interest prio, distance, discoveredFocus)
    
    val nextStep = paths.firstStepToVec(focus vec).get.value
    val nextFocus = focus copy (vec = translate(focus vec, nextStep))
    
    (nextStep, nextFocus)
  }
  
  private[this] def previousOrDiscoveredFocus(prio: Int, distance: Long, discoveredFocus: Focus): Focus =
    (for {
      f @ Focus(v, c, p) <- state.lastFocus    if p >= prio
      d                  <- paths distanceTo v if d > 0
      currentContent      = view area v
      stillValid          = currentContent == c || currentContent == Fog || c == Fog
    } yield 
      if (p == prio || stillValid) f 
      else discoveredFocus
    ) getOrElse discoveredFocus
  
  
  private[this] def translatedAndFadedTrail(nextStep: Vec): List[Trail] = {
    val first = Trail(center, (view.len * 3L) / 2L + 2L)
    val nextTrail = first :: state.trail.takeWhile(_.discouragement > 0)
    
    nextTrail map { t =>
      t.copy(vec = translate(t.vec, nextStep), discouragement = (t.discouragement * 2L) / 3L)
    } 
  }
  
  private[this] def translate(v: Vec, t: Vec) = v + center - t
}
