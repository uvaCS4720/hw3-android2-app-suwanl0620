package edu.nd.pmcburne.hwapp.one

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.local.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.model.Game
import edu.nd.pmcburne.hwapp.one.data.remote.EspnApiService
import edu.nd.pmcburne.hwapp.one.data.repository.GamesRepository
import edu.nd.pmcburne.hwapp.one.util.ConnectivityObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val api = EspnApiService.create()
    private val connectivity = ConnectivityObserver(application)
    private val repository = GamesRepository(api, db.gameDao(), connectivity)

    // Date format "yyyy/MM/dd"
    private val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

    var selectedDate by mutableStateOf(today)
        private set
    var selectedGender by mutableStateOf("men")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isOffline by mutableStateOf(false)
        private set

    val games: StateFlow<List<Game>> = snapshotFlow { selectedDate to selectedGender }
        .flatMapLatest { (date, gender) -> repository.getGames(date, gender) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { refresh() }

    fun setDate(date: String) {
        selectedDate = date
        refresh()
    }

    fun setGender(gender: String) {
        selectedGender = gender
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isOffline = !connectivity.isConnected()
            isLoading = true
            try {
                repository.refreshGames(selectedDate, selectedGender)
            } catch (e: Exception) {
                // Network error; local DB still serve cached data
            } finally {
                isLoading = false
            }
        }
    }
}