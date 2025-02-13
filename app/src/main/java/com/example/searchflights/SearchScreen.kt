package com.example.searchflights

import FlightViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import models.Airport
import models.Favorite
import models.Flight

@Composable
fun SearchScreen(viewModel: FlightViewModel) {
    val query by viewModel.searchQuery.collectAsState()
    var text by remember { mutableStateOf(query) }

    LaunchedEffect(query) {
        text = query
    }

    val airports by viewModel.airports.collectAsState()
    val flights by viewModel.flights.collectAsState()
    val selectedAirport by viewModel.selectedAirport.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by remember { derivedStateOf { text.isNotEmpty() && airports.isEmpty() } }
    val isFlightsLoading by remember { derivedStateOf { selectedAirport != null && flights.isEmpty() } }

    val isInputDisabled = selectedAirport != null
    val airportCode = selectedAirport?.iataCode ?: text

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp))

        Text(
            text = stringResource(R.string.flight_search),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SearchAirportField(
            airportCode = airportCode,
            isInputDisabled = isInputDisabled,
            onAirportCodeChange = {
                text = it
                viewModel.searchAirports(it)
            },
            onClearSelection = {
                viewModel.clearSelection()
                text = ""
            }
        )


        if (isLoading) {
            LoadingIndicator()
        }

        if (text.isNotEmpty() && selectedAirport == null) {
            AirportList(airports = airports, onAirportSelect = { airport ->
                viewModel.selectAirport(airport)
                viewModel.generateFlightsForAirport(airport)
            })
        }

        if (selectedAirport != null) {
            FlightList(
                flights = flights,
                favorites = favorites,
                isFlightsLoading = isFlightsLoading,
                onFavoriteToggle = { flight ->
                    val isFavorite = favorites.any { it.departureCode == flight.departureCode && it.destinationCode == flight.destinationCode }
                    if (isFavorite) {
                        viewModel.removeFavorite(flight.departureCode, flight.destinationCode)
                    } else {
                        viewModel.addFavorite(flight.departureCode, flight.destinationCode)
                    }
                }
            )
        } else if (text.isEmpty()) {
            FavoriteFlightList(favorites = favorites, onRemoveFavorite = { favorite ->
                viewModel.removeFavorite(favorite.departureCode, favorite.destinationCode)
            })
        }
    }
}

@Composable
fun SearchAirportField(
    airportCode: String,
    isInputDisabled: Boolean,
    onAirportCodeChange: (String) -> Unit,
    onClearSelection: () -> Unit
) {
    OutlinedTextField(
        value = airportCode,
        onValueChange = onAirportCodeChange,
        label = { Text(stringResource(R.string.label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        enabled = !isInputDisabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            disabledPrefixColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary
        )
    )


    if (isInputDisabled) {
        Button(
            onClick = onClearSelection,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(R.string.back))
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AirportList(airports: List<Airport>, onAirportSelect: (Airport) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(airports) { airport ->
            AirportCard(airport = airport, onClick = { onAirportSelect(airport) })
        }
    }
}

@Composable
fun AirportCard(airport: Airport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = airport.iataCode,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = airport.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun FlightList(
    flights: List<Flight>,
    favorites: List<Favorite>,
    isFlightsLoading: Boolean,
    onFavoriteToggle: (Flight) -> Unit
) {
    if (isFlightsLoading) {
        LoadingIndicator()
    } else if (flights.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(flights) { flight ->
                FlightCard(flight = flight, isFavorite = favorites.any { it.departureCode == flight.departureCode && it.destinationCode == flight.destinationCode }, onFavoriteToggle = onFavoriteToggle)
            }
        }
    } else {
        Text(text = stringResource(R.string.no_flights_available), modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun FlightCard(flight: Flight, isFavorite: Boolean, onFavoriteToggle: (Flight) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(R.string.from, flight.departureCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.to, flight.destinationCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { onFavoriteToggle(flight) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.Star,
                    contentDescription = stringResource(R.string.favorite),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun FavoriteFlightList(favorites: List<Favorite>, onRemoveFavorite: (Favorite) -> Unit) {
    if (favorites.isNotEmpty()) {
        Text(
            text = stringResource(R.string.favorite_flights),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favorites) { favorite ->
                FavoriteFlightCard(favorite = favorite, onRemoveFavorite = onRemoveFavorite)
            }
        }
    }
}

@Composable
fun FavoriteFlightCard(favorite: Favorite, onRemoveFavorite: (Favorite) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "From: ${favorite.departureCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "To: ${favorite.destinationCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { onRemoveFavorite(favorite) }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.remove_from_favorites),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}