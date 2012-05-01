package eu.flierl.pandacub

object EndOfRound extends ((BotState, Int) => State) {
  def apply(state: BotState, energy: Int): State = {
    val round = state.scores.length + 1
    val scores = energy :: state.scores
    val average = (scores take 20 map (_.toLong) sum) / scores.length
    println("PandaCub had %d energy after round %d." format (energy, round))
    println("PandaCub's average score in the 20 recent matches is %d." format average)
    (state.copy(scores = scores), "")
  }
}