package repositories

import daos.FavouriteDao
import kotlinx.coroutines.flow.Flow
import models.Favorite

class FavoriteRepository(private val favouriteDao: FavouriteDao) {
    fun getAll(): Flow<List<Favorite>> = favouriteDao.getAll()

    suspend fun insertFavorite(favorite: Favorite) = favouriteDao.insertFavorite(favorite)

    suspend fun deleteFavorite(departureCode: String, destinationCode: String) =
        favouriteDao.deleteFavorite(departureCode, destinationCode)

}