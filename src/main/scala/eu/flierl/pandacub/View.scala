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
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.WUnDiEdge
import collection.breakOut

final case class View(len: Int, area: Map[Vec, Cell]) {
  lazy val inverse = area.groupBy(_._2).mapValues(_.keySet)
  
  def all(c: Cell): Set[Vec] = inverse.getOrElse(c, Set())
  
  def graph(last: Map[Vec, Long] = Map()): G = (for {
    v <- area.keys filter isSafe
    n <- southEastNeighboursOf(v)
  } yield v ~ n % weight(v, n, last))(breakOut)
  
  def weight(a: Vec, b: Vec, last: Map[Vec, Long]): Long =
    last.get(a).orElse(last.get(b)).getOrElse(1L)
  
  private[this] def southEastNeighboursOf(v: Vec): Seq[Vec] =
    List(Vec(1, 0), Vec(0, 1), Vec(1, 1), Vec(1, -1)) map (v+) filter area.contains filter isSafe
  
  def isSafe(v: Vec) = area(v) match {
    case Wall | Tiger | Kitty | Shroom | Snorg => false
    case _ => true
  }
  
  override def toString = Show.show(this).sliding(len, len).mkString("\n")
}

object View extends (List[Cell] => View) {
  def apply(cells: List[Cell]): View = {
    require((cells length) % 2 == 1, "there must be an odd number of cells")
    val n = sqrt(cells length)
    View(n, (for ((cell, index) <- cells.zipWithIndex) yield pair(n, index, cell)) toMap)
  }
  
  private def pair(n: Int, i: Int, c: Cell) = Vec(i % n, i / n) -> c
}
