package edu.nd.pmcburne.hwapp.one.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import edu.nd.pmcburne.hwapp.one.MainViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Full screen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val games by viewModel.games.collectAsState()
    val isLoading = viewModel.isLoading
    val isOffline = viewModel.isOffline

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("College Basketball Scores") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Offline banner
            if (isOffline) {
                Surface(color = MaterialTheme.colorScheme.errorContainer) {
                    Text("Offline — showing cached scores",
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        style = MaterialTheme.typography.labelMedium)
                }
            }

            // Controls row
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                // Date button
                OutlinedButton(onClick = { showDatePicker = true }) {
                    Text(viewModel.selectedDate.replace("/", "-"))
                }

                // Gender toggle
                Row {
                    listOf("men", "women").forEach { gender ->
                        FilterChip(
                            selected = viewModel.selectedGender == gender,
                            onClick = { viewModel.setGender(gender) },
                            label = { Text(gender.replaceFirstChar { it.uppercase() }) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Game list with pull-to-refresh
            SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.refresh() }) {
                LazyColumn {
                    if (games.isEmpty() && !isLoading) {
                        item {
                            Text("No games found for this date.",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    items(games, key = { it.id }) { game ->
                        GameCard(game = game, gender = viewModel.selectedGender)
                    }
                }
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        val formatted = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        viewModel.setDate(formatted)
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}