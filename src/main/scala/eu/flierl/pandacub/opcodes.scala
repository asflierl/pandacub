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

import java.io.File

sealed trait OpcodeFromServer

case class Welcome(name: String, path: File, apocalypse: Int, round: Int) extends OpcodeFromServer

sealed trait React extends OpcodeFromServer {
  def name: String
  def time: Int
  def view: View
  def energy: Int
}

case class MasterReact(name: String, time: Int, view: View, energy: Int) extends React
  
case class MiniReact(generation: Int, name: String, time: Int, view: View,
  energy: Int, master: Vec) extends React

case class Goodbye(energy: Int) extends OpcodeFromServer

sealed trait OpcodeFromBot {
  def +: (first: OpcodeFromBot) = first :: this :: Nil  
}

case class Move(direction: Vec) extends OpcodeFromBot

case class Spawn(direction: Vec, name: String, energy: Int) extends OpcodeFromBot

case class Explode(size: Int) extends OpcodeFromBot

case class Say(text: String) extends OpcodeFromBot

case class Status(text: String) extends OpcodeFromBot

case class Log(text: String) extends OpcodeFromBot
