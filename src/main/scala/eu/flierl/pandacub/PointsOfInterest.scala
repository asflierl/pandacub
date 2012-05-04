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

abstract class PointsOfInterest(state: BotState, view: View, isMaster: Boolean) {
  import view.center
  
  private[this] val paths = new ShortestPaths(view graph discouragements, center)
  private[this] def discouragements = state.trailMap ++ enemies
  private[this] def enemies = (view all Snorg).view flatMap view.neighbours map (_ -> 25L)
  
  def closest(interests: Interest*) = best(interests, _ => true, _.minBy(_._1)) 
  
  def farthest(interests: Interest*) = best(interests, _ => true, _.maxBy(_._1))
  
  def median(interests: Interest*) = best(interests, view isEdge, aroundMedian)
  
  def directionOf(v: Vec, interests: Interest*) = best(interests, _ => true, direction(v))
  
  private[this] def aroundMedian(s: Seq[Ω]): Ω = selectRandomly(
    s.sortBy(_._1).zipWithIndex.filter(t => math.abs(t._2 - (s.size / 2)) < 5).map(_._1))
  
  private[this] def direction(v: Vec)(s: Seq[Ω]): Ω = s minBy { case (d, t, i) =>
    val a = v.x - t.x
    val b = v.y - t.y
    sqrt(a * a + b * b) + 3L * d
  }
    
  val confused = (state, show(Status("*confused*")))
  
  type Ω = (Long, Vec, Interest) // distance, target, interest
  
  def best(interests: Seq[Interest], incl: Vec => Boolean, selectFrom: Seq[Ω] => Ω): Option[OpWithState] =
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
  
  private def move(desire: Ω): Some[OpWithState] = {
    val (distance, target, interest) = desire
    val (nextStep, nextFocus) = findNextStepAndFocus(distance, target, interest)
   
    Some((state.copy(trail = translatedAndFadedTrail(nextStep), lastFocus = Some(nextFocus)), 
          show(Move(nextStep - center) :: (if (isMaster) List(Status(interest.status)) else Nil))))
  }
  
  private[this] def findNextStepAndFocus(distance: Long, target: Vec, interest: Interest): (Vec, Focus) = {
    val discoveredFocus = Focus(target, interest cell, interest prio)
    
    val focus = previousOrDiscoveredFocus(interest prio, distance, discoveredFocus)
    
    val nextStep = paths.firstStepTo(focus vec).get
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
      if (discoveredFocus.cell == Panda) discoveredFocus
      else if (p == prio && stillValid) f
      else if (stillValid) f
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
