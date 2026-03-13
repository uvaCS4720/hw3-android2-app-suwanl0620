package edu.nd.pmcburne.hwapp.one.data.repository

import android.util.Log
import edu.nd.pmcburne.hwapp.one.data.local.GameDao
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity
import edu.nd.pmcburne.hwapp.one.data.model.Game
import edu.nd.pmcburne.hwapp.one.data.model.GameData
import edu.nd.pmcburne.hwapp.one.data.model.GameStatus
import edu.nd.pmcburne.hwapp.one.data.remote.EspnApiService
import edu.nd.pmcburne.hwapp.one.util.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// SSOT; decide where data comes from
class GamesRepository(
    private val apiService: EspnApiService,
    private val gameDao: GameDao,
    private val connectivity: ConnectivityObserver
) {
    // Observe local DB; always returns cached data
    fun getGames(date: String, gender: String): Flow<List<Game>> {
        return gameDao.getGames(date, gender).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Fetch from network and save to db
    // Logging for debugs
    suspend fun refreshGames(date: String, gender: String) {
        if (!connectivity.isConnected()) {
            Log.d("REPO", "Not connected, skipping fetch")
            return
        }
        val parts = date.split("/")
        Log.d("REPO", "Fetching: gender=$gender date=$date")

        try {
            val response = apiService.getScoreboard(gender, parts[0], parts[1], parts[2])
            Log.d("REPO", "Games count: ${response.games?.size}")

            val entities = response.games?.mapNotNull { wrapper ->
                wrapper.game?.toEntity(gender, date)
            } ?: run {
                Log.d("REPO", "games was null")
                return
            }

            Log.d("REPO", "Inserting ${entities.size} entities")
            gameDao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("REPO", "Error: ${e.message}", e)
        }
    }

    // Extension function to convert between layers
    fun GameData.toEntity(gender: String, date: String): GameEntity {
        // gameState is "final", "live", or "pre"
        val statusName = when (gameState.lowercase()) {
            "final" -> "final"
            "live"  -> "in_progress"
            else    -> "upcoming"
        }

        val homeName = home.names.short
            ?: home.names.full
            ?: home.names.char6
            ?: "Unknown"

        val awayName = away.names.short
            ?: away.names.full
            ?: away.names.char6
            ?: "Unknown"

        return GameEntity(
            id          = gameID,
            homeTeam    = homeName,
            awayTeam    = awayName,
            homeScore   = home.score?.toIntOrNull(),
            awayScore   = away.score?.toIntOrNull(),
            statusName  = statusName,
            startTime   = startTime,
            period      = null,
            clock       = contestClock,
            homeIsWinner = home.winner,
            gender      = gender,
            date        = date
        )
    }

}
fun GameEntity.toDomain(): Game {
    val status = when (statusName) {
        "in_progress" -> GameStatus.InProgress
        "final" -> GameStatus.Final
        else -> GameStatus.Upcoming
    }
    return Game(id, homeTeam, awayTeam, homeScore, awayScore,
        status, startTime, period, clock, homeIsWinner, gender, date)
}