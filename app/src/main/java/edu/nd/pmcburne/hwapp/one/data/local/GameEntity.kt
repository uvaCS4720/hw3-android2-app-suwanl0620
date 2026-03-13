package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.PrimaryKey
import androidx.room.Entity

// Room db version of game
@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val statusName: String,
    val startTime: String?,
    val period: Int?,
    val clock: String?,
    val homeIsWinner: Boolean?,
    val gender: String,
    val date: String
)