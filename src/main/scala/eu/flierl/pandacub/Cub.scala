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

import Interests._
import Show.show
import Cells.{ Panda, Cub }

final class Cub(state: BotState, apocalypse: Int) {
  def react(time: Int, view: View, energy: Int, master: Vec): OpWithState =
    if (apocalypse - time > 250)
      decideBasedOn(state, altered(view, energy)) nextMove
    else 
      homeSick(master)(state, view) nextMove
    
  private[this] val decideBasedOn = 
    new PointsOfInterest(_: BotState, _: View, false) with MovementDecision {
      def nextMove =
        closest (InterestingPanda)                       orElse (
        closest (InterestingFluppet, InterestingBamboo)) orElse (
        median  (InterestingEmpty))                      orElse (
        farthest(InterestingEmpty))                      orElse (
        median  (InterestingFog))                        orElse (
        farthest(InterestingFog))                     getOrElse (
        confused)
    }
  
  private[this] def homeSick(master: Vec) = 
    new PointsOfInterest(_: BotState, _: View, false) with MovementDecision {
      def nextMove =
        closest            (InterestingPanda)      orElse (
        directionOf(master, InterestingFluppet, 
                            InterestingBamboo, 
                            InterestingEmpty, 
                            InterestingFog))    getOrElse (
        confused)
    }
  
  private[this] def altered(view: View, energy: Int): View = 
    if (energy > 11111) view exclude Cub 
    else view exclude (Panda, Cub) 
}
