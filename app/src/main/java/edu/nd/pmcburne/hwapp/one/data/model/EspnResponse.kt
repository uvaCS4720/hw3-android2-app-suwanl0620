package edu.nd.pmcburne.hwapp.one.data.model

import com.google.gson.annotations.SerializedName

// Matches JSON from API
// SerializedName fields will map to key in JSON
data class EspnResponse(
    @SerializedName("games") val games: List<GameWrapper>? = null
)

data class GameWrapper(
    @SerializedName("game") val game: GameData? = null
)

data class GameData(
    @SerializedName("gameID") val gameID: String,
    @SerializedName("gameState") val gameState: String,
    @SerializedName("startTime") val startTime: String? = null,
    @SerializedName("currentPeriod") val currentPeriod: String? = null,
    @SerializedName("contestClock") val contestClock: String? = null,
    @SerializedName("home") val home: TeamData,
    @SerializedName("away") val away: TeamData
)

data class TeamData(
    @SerializedName("score") val score: String? = null,
    @SerializedName("winner") val winner: Boolean = false,
    @SerializedName("names") val names: TeamNames
)

data class TeamNames(
    @SerializedName("short") val short: String? = null,
    @SerializedName("full") val full: String? = null,
    @SerializedName("char6") val char6: String? = null
)