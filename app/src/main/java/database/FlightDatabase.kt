package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import daos.AirportDao
import daos.FavoriteDao
import models.Airport
import models.Favorite

@Database(entities = [Airport::class, Favorite::class], version = 1)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java,
                    "flight_search.db"
                )
                    .createFromAsset("flight_search.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}