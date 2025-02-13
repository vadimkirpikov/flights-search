package com.example.searchflights

import FlightViewModel
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.searchflights.ui.theme.SearchFlightsTheme
import database.FlightDatabase
import datastores.SearchPreferencesRepository
import viewmodels.FlightViewModelFactory


class MainActivity : ComponentActivity() {
    private val Context.dataStore by preferencesDataStore(name = "search_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = FlightDatabase.getDatabase(applicationContext)
        val airportDao = database.airportDao()
        val favoriteDao = database.favoriteDao()
        val searchPreferencesRepository = SearchPreferencesRepository(dataStore)

        val viewModelFactory = FlightViewModelFactory(airportDao, favoriteDao, searchPreferencesRepository)

        setContent {
            SearchFlightsTheme(darkTheme = isSystemInDarkTheme(), dynamicColor = false) {
                val viewModel: FlightViewModel = viewModel(factory = viewModelFactory)

                SearchScreen(viewModel = viewModel)
            }

        }
    }
}