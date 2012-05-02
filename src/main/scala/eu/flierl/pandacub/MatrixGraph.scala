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

import scala.collection.mutable.BitSet

final class MatrixGraph(viewLen: Int) extends Graph {
  import MatrixGraph.directions
  
  private[this] val matrixLen = viewLen * viewLen
  private[this] val matrixCells = matrixLen * matrixLen
  private[this] val matrix = makeMatrix
  private[this] val nodeSet = BitSet()

  @inline private[this] def makeMatrix: Array[Long] = {
    val m = Array.ofDim[Long](matrixCells)
    var i = 0
    while (i < matrixCells) {
      m(i) = -1L
      i += 1
    }
    m
  }
  
  def areaSize = viewLen
  
  def contains(v: Vec): Boolean = nodeSet contains cell(v)
  
  def add(mx: Vec, my: Vec, weight: Long): Unit = {
    val i = idx(min(mx, my), max(mx, my))
    matrix(i) = weight
    nodeSet += cell(mx)
    nodeSet += cell(my)
  }
  
  def edge(mx: Vec, my: Vec): Long = {
    val i = idx(min(mx, my), max(mx, my))
    if (i < 0 || i >= matrix.length) -1L
    else matrix(i)
  }
  
  def nodes: Iterable[Vec] = nodeSet.view map rev
  
  def neighboursOf(v: Vec) = directions.view map (v+) map (t => (t, edge(v, t))) filter (_._2 >= 0) iterator
  
  def edgeCount = matrix count (0<=)

  private[this] def cell(v: Vec): Int = v.y * viewLen + v.x
  private[this] def rev(i: Int): Vec = Vec(i % viewLen, i / viewLen)
  private[this] def idx(mx: Vec, my: Vec): Int = cell(my) * matrixLen + cell(mx)
  private[this] def min(mx: Vec, my: Vec): Vec = if (Vec.compare(mx, my) <= 0) mx else my
  private[this] def max(mx: Vec, my: Vec): Vec = if (Vec.compare(mx, my) > 0) mx else my
}
object MatrixGraph {
  private[MatrixGraph] val directions = 
    List(Vec( 1, 0), Vec(0,  1), Vec( 1,  1), Vec( 1, -1),
         Vec(-1, 0), Vec(0, -1), Vec(-1, -1), Vec(-1,  1))
         
  def apply(len: Int, edges: (Vec, Vec, Long)*): MatrixGraph = {
    val g = new MatrixGraph(len)
    edges foreach { case (mx, my, w) => g add (mx, my, w) }
    g
  }
}