package scalatron.botwar.botPlugin

class ControlFunctionFactory {
  def create: String => String = _ => "Status(text=Hello)" 
}