package scalatron.botwar.botPlugin

import eu.flierl.pandacub.Panda
import eu.flierl.pandacub.BotState
import eu.flierl.pandacub.State
import eu.flierl.pandacub.=/>
import java.util.concurrent.atomic.AtomicReference
import eu.flierl.pandacub.BotState
import eu.flierl.pandacub.Router.parseAndRoute

class ControlFunctionFactory {
  lazy val create: String => String = loadState andThen parseAndRoute andThen storeState 
  
  private[this] def loadState = (s: String) => (botState get, s) 
  
  private[this] def storeState: State =/> String = {
    case (state, op) =>
      botState set state
      op
  } 
  
  private[this] lazy val botState = new AtomicReference[BotState](BotState())
}