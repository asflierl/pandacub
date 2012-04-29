package eu.flierl.pandacub

object Utils {
  def viewFrom(s: String): View = Grammar.parseAll(Grammar.view, s).get
}