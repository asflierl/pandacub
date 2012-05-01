import eu.flierl.pandacub.{ =/>, State, BotState }
import eu.flierl.pandacub.Router.parseAndRoute
import java.util.concurrent.atomic.AtomicReference

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