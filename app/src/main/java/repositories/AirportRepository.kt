package repositories

import daos.AirportDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import models.Airport
import models.Flight

class AirportRepository(private val airportDao: AirportDao) {


    fun searchAirports(query: String): Flow<List<Airport>> {
        val formattedQuery = "%$query%"
        return airportDao.searchAirports(formattedQuery)
    }

    fun generateFlightsForAirports(): Flow<List<Flight>> = flow {
        val airports = getAllAirports()

        val flights = mutableListOf<Flight>()

        for (departureAirport in airports) {
            for (destinationAirport in airports) {
                if (departureAirport.id != destinationAirport.id) {
                    val flight = Flight(
                        departureCode = departureAirport.iataCode,
                        destinationCode = destinationAirport.iataCode
                    )
                    flights.add(flight)
                }
            }
        }
        emit(flights)
    }

    private suspend fun getAllAirports(): List<Airport> {
        return airportDao.getAll().first()
    }
}