package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Looper
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.DatabasePictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.lang.Exception

enum class AsteroidsFilter { WEEK, TODAY, ALL }
enum class Status { ERROR, DONE }

class MainViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)

    private val asteroidsRepository = AsteroidsRepository(database)

    private val _asteroids = MutableLiveData<List<Asteroid>>()

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()

    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _status = MutableLiveData<Status>()

    val status: LiveData<Status>
        get() = _status

    init {
        viewModelScope.launch {
            try{
                asteroidsRepository.refresh()
                _asteroids.value = asteroidsRepository.getAsteroids(AsteroidsFilter.WEEK)
                _pictureOfDay.value = asteroidsRepository.getPictureOfDay()
                _status.value = Status.DONE
            } catch (e: Exception){
                _status.value = Status.ERROR
                _asteroids.value = asteroidsRepository.getAsteroids(AsteroidsFilter.WEEK)
                _pictureOfDay.value = asteroidsRepository.getPictureOfDay()
            }
        }
    }

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid>()

    val navigateToAsteroidDetails: LiveData<Asteroid>
        get() = _navigateToAsteroidDetails

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToAsteroidDetails.value = null
    }

    fun updateAsteroids(filter: AsteroidsFilter){
        viewModelScope.launch {
            _asteroids.value = asteroidsRepository.getAsteroids(filter)
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}