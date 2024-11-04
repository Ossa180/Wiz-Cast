import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.Model.DataBase.WeatherDao
import com.example.wiz_cast.Model.DataBase.WeatherDatabase
import com.example.wiz_cast.Model.DataBase.WeatherLocalDataSourceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class WeatherLocalDataSourceImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var weatherLocalDataSource: WeatherLocalDataSourceImpl

    @Before
    fun setup() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        weatherDao = database.weatherDao()
        weatherLocalDataSource = WeatherLocalDataSourceImpl(weatherDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveFavoriteLocation_retrievesLocation() = runTest {
        // Given: A favorite location to save
        val favoriteLocation = FavoriteLocation(latitude = 25.0, longitude = 45.0, name = "Test Location", description = "Test Description", temperature = 25.0, weatherIcon = "01d")

        // When: Saving the location
        weatherLocalDataSource.saveFavoriteLocation(favoriteLocation)

        // Then: The location can be retrieved
        val favoriteLocations = weatherLocalDataSource.getFavoriteLocations().first()
        assertThat(favoriteLocations.isNotEmpty(), `is`(true))
        assertThat(favoriteLocations[0].latitude, `is`(25.0))
        assertThat(favoriteLocations[0].longitude, `is`(45.0))
        assertThat(favoriteLocations[0].name, `is`("Test Location"))
    }

    @Test
    fun deleteFavoriteLocation_locationIsDeleted() = runTest {
        // Given: A favorite location saved
        val favoriteLocation = FavoriteLocation(latitude = 30.0, longitude = 50.0, name = "Location to Delete", description = "Test Description", temperature = 25.0, weatherIcon = "01d")
        weatherLocalDataSource.saveFavoriteLocation(favoriteLocation)

        // When: Deleting the location
        weatherLocalDataSource.deleteFavoriteLocation(30.0, 50.0)

        // Then: The location should no longer be in the database
        val favoriteLocations = weatherLocalDataSource.getFavoriteLocations().first()
        assertThat(favoriteLocations.isEmpty(), `is`(true))
    }
}
