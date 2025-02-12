package repositories

import daos.AirportDao
import kotlinx.coroutines.flow.Flow
import models.Airport

class AirportRepository(private val airportDao: AirportDao) {
    fun getAll(): Flow<List<Airport>> = airportDao.getAll()

    fun searchAirports(query: String): Flow<List<Airport>> = airportDao.searchAirports(query)
}