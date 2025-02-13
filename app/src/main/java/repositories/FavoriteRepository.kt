package repositories

import daos.FavoriteDao
import kotlinx.coroutines.flow.Flow
import models.Favorite

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    fun getAll(): Flow<List<Favorite>> = favoriteDao.getAll()

    suspend fun insertFavorite(favorite: Favorite) = favoriteDao.insertFavorite(favorite)

    suspend fun removeFavorite(departure: String, destination: String) = favoriteDao.removeFavorite(departure, destination)
}