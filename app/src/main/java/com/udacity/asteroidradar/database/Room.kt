package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import retrofit2.http.DELETE
import java.sql.Date

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid where closeApproachDate >= :today order by closeApproachDate asc")
    fun getNextSevenDaysAsteroids(today:String): List<DatabaseAsteroid>

    @Query("select * from databaseasteroid where closeApproachDate == :today order by closeApproachDate asc")
    fun getTodayAsteroids(today:String): List<DatabaseAsteroid>

    @Query("select * from databaseasteroid order by closeApproachDate asc")
    fun getAllAsteroids(): List<DatabaseAsteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg databaseAsteroids: DatabaseAsteroid)

    @Query("delete from databaseasteroid where closeApproachDate < :today")
    fun deleteOldData(today: String)
}

@Dao
interface PictureOfDayDao{
    @Query("select * from databasepictureofday order by id desc limit 1")
    fun getPictureOfDay(): DatabasePictureOfDay

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)
}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 4)
abstract class AsteroidsDatabase: RoomDatabase(){
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase{
    synchronized(AsteroidsDatabase::class.java){
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}