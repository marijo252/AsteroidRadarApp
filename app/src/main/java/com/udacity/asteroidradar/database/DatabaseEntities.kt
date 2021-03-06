package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

@Entity
data class DatabasePictureOfDay constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mediaType: String,
    val title: String,
    val url: String
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid>{
    return map{
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun ArrayList<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid>{
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

fun toDomainModel(dbPictureOfDay: DatabasePictureOfDay): PictureOfDay{
    return PictureOfDay(
        mediaType = dbPictureOfDay.mediaType,
        title = dbPictureOfDay.title,
        url = dbPictureOfDay.url
    )
}

fun toDatabaseModel(pictureOfDay: PictureOfDay): DatabasePictureOfDay{
    return DatabasePictureOfDay(
        mediaType = pictureOfDay.mediaType,
        title = pictureOfDay.title,
        url = pictureOfDay.url
    )
}