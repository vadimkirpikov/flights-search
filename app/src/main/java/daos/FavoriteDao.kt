package daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import models.Favorite

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departure AND destination_code = :destination")
    suspend fun removeFavorite(departure: String, destination: String)
}