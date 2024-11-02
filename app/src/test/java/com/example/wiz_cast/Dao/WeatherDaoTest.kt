package com.example.wiz_cast.Model.DataBase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wiz_cast.Model.DataBase.WeatherDatabase
import com.example.wiz_cast.Model.DataBase.WeatherDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class WeatherDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        // Initialize in-memory database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        weatherDao = database.weatherDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun removeFavoriteLocationByName_andCheckIfItIsDeleted() = runTest {
        // Given a FavoriteLocation
        val location = FavoriteLocation(
            name = "Test City",
            latitude = 10.0,
            longitude = 20.0,
            description = "Clear sky",
            temperature = 25.5,
            weatherIcon = "01d"
        )
        weatherDao.insertFavoriteLocation(location)

        // When removing the location by name
        weatherDao.removeFavoriteLocationByName(location.name)

        // Then retrieving the list should be empty
        val locations = weatherDao.getAllFavoriteLocations().first()
        assertThat(locations.isEmpty(), `is`(true))
    }

    @Test
    fun checkIfLocationIsFavorite() = runTest {
        // Given a FavoriteLocation
        val location = FavoriteLocation(
            name = "Test City",
            latitude = 10.0,
            longitude = 20.0,
            description = "Clear sky",
            temperature = 25.5,
            weatherIcon = "01d"
        )
        weatherDao.insertFavoriteLocation(location)

        // When checking if the location is favorite by name
        val isFavorite = weatherDao.isLocationFavorite("Test City")

        // Then the result should be 1 (true)
        assertThat(isFavorite, `is`(1))
    }

    @Test
    fun insertAndDeleteFavoriteLocation_byLatLong() = runTest {
        // Given a FavoriteLocation
        val location = FavoriteLocation(
            name = "Test City",
            latitude = 10.0,
            longitude = 20.0,
            description = "Clear sky",
            temperature = 25.5,
            weatherIcon = "01d"
        )
        weatherDao.insertFavoriteLocation(location)

        // When deleting by latitude and longitude
        weatherDao.removeFavoriteLocation(location.latitude, location.longitude)

        // Then it should not exist in the database
        val locations = weatherDao.getAllFavoriteLocations().first()
        assertThat(locations.isEmpty(), `is`(true))
    }
}
