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
import collection.breakOut

final case class View(len: Int, area: Map[Vec, Cell], exclude: Set[Cell] = Set()) {
  val center = Vec(len / 2, len / 2)
  lazy val inverse = area.groupBy(_._2).mapValues(_.keySet)
  
  def all(c: Cell): Set[Vec] = inverse.getOrElse(c, Set())
  
  def graph(discouragements: Map[Vec, Long] = Map()): Graph = {
    val g = new MatrixGraph(len)
    for {
      v <- area.keys filter isSafe
      n <- southEastNeighboursOf(v)
    } g.add(v, n, weight(v, n, discouragements))
    g
  }
  
  def weight(a: Vec, b: Vec, discouragements: Map[Vec, Long]): Long =
    discouragements get a orElse (discouragements get b) getOrElse 1L
  
  private[this] def southEastNeighboursOf(v: Vec): Seq[Vec] =
    List(Vec(1, 0), Vec(0, 1), Vec(1, 1), Vec(1, -1)).view map (v+) filter area.contains filter isSafe
    
  def neighbours(v: Vec): Seq[Vec] =
    List(Vec( 1, 0), Vec(0,  1), Vec( 1,  1), Vec( 1, -1),
         Vec(-1, 0), Vec(0, -1), Vec(-1, -1), Vec(-1,  1)
    ).view map (v+) filter area.contains filter isSafe
  
  def isSafe(v: Vec) = if (v == center) true else area(v) match {
    case Wall | Tiger | Kitty | Shroom | Snorg => false
    case c => ! exclude.contains(c)
  }
  
  def isEdge(v: Vec) = v.x < 3 || v.x > len - 4 || v.y < 3 || v.y > len - 4
  
  def exclude(ex: Cell*): View = copy(exclude = exclude ++ ex.toSet)
  
  override def toString = Show.show(this).sliding(len, len).mkString("\n")
}
