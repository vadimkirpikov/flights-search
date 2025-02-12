package viewmodels

import datastores.SearchPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import models.Airport
import models.Favorite
import repositories.AirportRepository
import repositories.FavoriteRepository

class FlightViewModel(private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository,
    private val searchPreferences: SearchPreferencesRepository) {

    private val _airports  = MutableStateFlow<List<Airport>>(emptyList())
    val airports = _airports.asStateFlow()

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val
}