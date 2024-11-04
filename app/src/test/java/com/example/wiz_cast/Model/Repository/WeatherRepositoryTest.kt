package com.example.wiz_cast.Model.Repository

import com.example.wiz_cast.FakeRepo.FakeWeatherLocalDataSourceImpl
import com.example.wiz_cast.FakeRepo.FakeWeatherRemoteDataSourceImpl
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Model.Pojo.Clouds
import com.example.wiz_cast.Model.Pojo.Coord
import com.example.wiz_cast.Model.Pojo.CurrentWeather
import com.example.wiz_cast.Model.Pojo.FiveDaysWeather
import com.example.wiz_cast.Model.Pojo.Main
import com.example.wiz_cast.Model.Pojo.Sys
import com.example.wiz_cast.Model.Pojo.Weather
import com.example.wiz_cast.Model.Pojo.Wind
import com.example.wiz_cast.Network.FiveDayForecastState
import com.example.wiz_cast.Network.WeatherState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    @Test
    fun fetchWeather_successfulResponse_emitsSuccessState() = runTest {
        // Given: A remote data source with a successful response
        val expectedWeather = CurrentWeather(
            base = "stations",
            clouds = Clouds(all = 90),
            cod = 200,
            coord = Coord(lat = 25.0, lon = 45.0),
            dt = 1631644800,
            id = 123456,
            main = Main(
                feels_like = 22.0,
                grnd_level = 1012,
                humidity = 80,
                pressure = 1013,
                sea_level = 1013,
                temp = 20.0,
                temp_max = 21.0,
                temp_min = 19.0
            ),
            name = "Test City",
            sys = Sys(
                country = "EG",
                id = 1,
                sunrise = 1631600400,
                sunset = 1631643600,
                type = 1
            ),
            timezone = 7200,
            visibility = 10000,
            weather = listOf(
                Weather(
                    description = "clear sky",
                    icon = "01d",
                    id = 800,
                    main = "Clear"
                )
            ),
            wind = Wind(
                deg = 180,
                gust = 3.5,
                speed = 2.0
            )
        )

        val fakeRemoteDataSource = FakeWeatherRemoteDataSourceImpl(
            weatherResponse = Response.success(expectedWeather)
        )
        val fakeLocalDataSource = FakeWeatherLocalDataSourceImpl()
        val repository = WeatherRepository(fakeRemoteDataSource, fakeLocalDataSource)

        // When: Collecting all results from the flow
        val results = repository.fetchWeather(30.0, 30.0, "fake_app_id", "metric", "en").toList()

        // Advance time until all delays (if any) and emissions complete
        advanceUntilIdle() // to avoid loading state error

        // Then: Check that the results contain Loading and Success states
        assert(results.any { it is WeatherState.Loading }) { "Expected a Loading state but got $results" }
        assert(results.any { it is WeatherState.Success }) { "Expected a Success state but got $results" }

        // Validate the Success state with the expected weather data
        val successState = results.filterIsInstance<WeatherState.Success>().first()
        assertThat(successState.weather, `is`(expectedWeather))
    }



    @Test
    fun addFavoriteLocation_retrievesLocation() = runTest {
        // Given: A location to be saved as favorite
        val favoriteLocation = FavoriteLocation(latitude = 25.0, longitude = 45.0, name = "Favorite Place", description = "Test Description", temperature = 25.0, weatherIcon = "01d")
        val fakeLocalDataSource = FakeWeatherLocalDataSourceImpl()
        val repository = WeatherRepository(FakeWeatherRemoteDataSourceImpl(), fakeLocalDataSource)

        // When: Saving the location and retrieving it
        repository.addFavoriteLocation(favoriteLocation)
        val result = repository.getFavoriteLocations().first()

        // Then: The retrieved list should contain the favorite location
        assertThat(result.isNotEmpty(), `is`(true))
        assertThat(result[0].latitude, `is`(25.0))
        assertThat(result[0].longitude, `is`(45.0))
        assertThat(result[0].name, `is`("Favorite Place"))
    }

    @Test
    fun removeFavoriteLocation_locationIsRemoved() = runTest {
        // Given: A location to be removed
        val favoriteLocation = FavoriteLocation(latitude = 30.0, longitude = 50.0, name = "Place to Remove", description = "Test Description", temperature = 25.0, weatherIcon = "01d")
        val fakeLocalDataSource = FakeWeatherLocalDataSourceImpl()
        fakeLocalDataSource.saveFavoriteLocation(favoriteLocation)
        val repository = WeatherRepository(FakeWeatherRemoteDataSourceImpl(), fakeLocalDataSource)

        // When: Removing the location
        repository.removeFavoriteLocation(30.0, 50.0)
        val result = repository.getFavoriteLocations().first()

        // Then: The list should be empty, indicating the location was removed
        assertThat(result.isEmpty(), `is`(true))
    }
}
