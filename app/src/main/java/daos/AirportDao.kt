package daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import models.Airport

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport")
    fun getAll(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE name LIKE :query OR name LIKE :query ORDER BY passengers DESC")
    fun searchAirports(query: String): Flow<List<Airport>>
}