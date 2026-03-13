package edu.nd.pmcburne.hwapp.one.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.nd.pmcburne.hwapp.one.data.model.Game
import edu.nd.pmcburne.hwapp.one.data.model.GameStatus

// Composable card that displays one game
@Composable
fun GameCard(game: Game, gender: String) {

    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status badge
            val statusText = when (game.status) {
                is GameStatus.Final -> "Final"
                is GameStatus.InProgress -> {
                    val clockDisplay = if (game.clock == "0:00" || game.clock.isNullOrBlank())
                        "In Progress"
                    else
                        game.clock
                    if (gender == "women") "Q${game.period ?: ""} · $clockDisplay"
                    else clockDisplay
                }
                is GameStatus.Upcoming -> game.startTime ?: "Upcoming"
            }
            Text(statusText, style = MaterialTheme.typography.labelSmall,
                color = when (game.status) {
                    is GameStatus.InProgress -> MaterialTheme.colorScheme.primary
                    is GameStatus.Final -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.secondary
                })

            Spacer(Modifier.height(8.dp))

            // Away team row
            TeamRow(
                name = game.awayTeam,
                score = game.awayScore,
                isWinner = game.homeIsWinner == false && game.status is GameStatus.Final,
                showScore = game.status !is GameStatus.Upcoming
            )
            // Home team row
            TeamRow(
                name = game.homeTeam,
                score = game.homeScore,
                isWinner = game.homeIsWinner == true && game.status is GameStatus.Final,
                showScore = game.status !is GameStatus.Upcoming
            )
        }
    }
}

@Composable
fun TeamRow(name: String, score: Int?, isWinner: Boolean, showScore: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isWinner) Text("▶ ", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary)
            Text(name,
                style = if (isWinner) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.bodyLarge)
        }
        if (showScore && score != null) {
            Text("$score",
                style = if (isWinner) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.titleLarge)
        }
    }
}