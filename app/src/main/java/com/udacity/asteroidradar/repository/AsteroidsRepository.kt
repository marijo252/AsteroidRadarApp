package com.udacity.asteroidradar.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.domain.Asteroid
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.*
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.main.AsteroidsFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    @RequiresApi(Build.VERSION_CODES.O)
    val startDate: String = LocalDateTime.now().format(ISO_LOCAL_DATE)

    suspend fun getAsteroids(filter: AsteroidsFilter): List<Asteroid>{
        return withContext(Dispatchers.IO) {
            when (filter) {
                AsteroidsFilter.WEEK -> database.asteroidDao.getNextSevenDaysAsteroids(startDate)
                    .asDomainModel()
                AsteroidsFilter.ALL -> database.asteroidDao.getAllAsteroids().asDomainModel()
                AsteroidsFilter.TODAY -> database.asteroidDao.getTodayAsteroids(startDate)
                    .asDomainModel()
            }
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay{
        return withContext(Dispatchers.IO) {
            toDomainModel(database.pictureOfDayDao.getPictureOfDay())
        }
    }

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val asteroidsNetwork = Network.retrofitService.getAsteroids(startDate, Constants.API_KEY)
            val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsNetwork))
            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
            val pictureOfDay = Network.retrofitService.getPictureOfDay(Constants.API_KEY)
            database.pictureOfDayDao.insertPictureOfDay(toDatabaseModel(pictureOfDay))
        }
    }

    suspend fun deletePreviousAsteroids(){
        withContext(Dispatchers.IO){
            database.asteroidDao.deleteOldData(startDate)
        }
    }
}