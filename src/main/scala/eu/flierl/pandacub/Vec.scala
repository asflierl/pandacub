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

class Vec(val x: Int, val y: Int) {
  def +(v: Vec) = Vec(x + v.x, y + v.y)
  def -(v: Vec) = Vec(x - v.x, y - v.y)
  
  override def equals(a: Any): Boolean = a match {
    case other: Vec => x == other.x && y == other.y
    case _ => false
  }
  
  override val hashCode = (17 * 31 + x) * 31 + y
  
  override val toString = "Vec(" + x + "," + y + ")"
}

object Vec extends ((Int, Int) => Vec) {
  private[this] val cache = Array.tabulate(200, 200)((x, y) => new Vec(x - 100, y - 100))
  
  def apply(x: Int, y: Int): Vec = 
    if (x >= -100 && x < 100 && y >= -100 && y < 100) cache(x + 100)(y + 100)
    else new Vec(x, y)
  
  def unapply(v: Vec): Option[(Int, Int)] = Some((v.x, v.y))
  
  @inline final def compare(a: Vec, b: Vec) = {
    val priorityCriterion = if (a.x < b.x) -1 else if (a.x == b.x) 0 else 1 
    if (priorityCriterion != 0) priorityCriterion
    else if (a.y < b.y) -1 else if (a.y == b.y) 0 else 1
  }
}
