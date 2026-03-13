package edu.nd.pmcburne.hwapp.one.data.model

// Representation for game
data class Game(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: GameStatus,
    val startTime: String?,
    val period: Int?,
    val clock: String?,
    val homeIsWinner: Boolean?,
    val gender: String,
    val date: String // "yyyy/MM/dd"
)

sealed class GameStatus {
    object Upcoming : GameStatus()
    object InProgress : GameStatus()
    object Final : GameStatus()
}