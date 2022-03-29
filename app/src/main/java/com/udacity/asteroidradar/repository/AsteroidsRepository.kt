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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()){
            toDomainModel(it ?: DatabasePictureOfDay(mediaType = "", title = "default", url = ""))
        }
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val startDate: String = LocalDateTime.now().format(ISO_LOCAL_DATE)

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val asteroidsNetwork = Network.retrofitService.getAsteroids(startDate, Constants.API_KEY)
            val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsNetwork))
            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
            val pictureOfDay = Network.retrofitService.getPictureOfDay(Constants.API_KEY)
            database.pictureOfDayDao.insertPictureOfDay(toDatabaseModel(pictureOfDay))
        }
    }
}