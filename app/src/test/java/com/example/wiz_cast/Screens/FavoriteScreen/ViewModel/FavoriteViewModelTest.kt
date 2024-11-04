package com.example.wiz_cast.Screens.FavoriteScreen.ViewModel

import MainCoroutineRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.wiz_cast.FakeRepo.FakeWeatherRepository
import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import com.example.wiz_cast.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
class FavoriteViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var repository: FakeWeatherRepository

    @Before
    fun setup() {
        repository = FakeWeatherRepository()
        viewModel = FavoriteViewModel(repository)
    }

    @Test
    fun addFavoriteLocation_updatesFavoriteLocationsState() = runTest {
        // Given: A FavoriteLocation to add
        val location = FavoriteLocation(25, "my name", 35.0, 35.0, "my desc", 35.5, "01d")
        repository.setFavoriteLocations(emptyList())

        // When: Adding the location
        viewModel.addFavoriteLocation(location)

        // Wait for the flow to emit the expected result
        advanceUntilIdle()  // Ensures all coroutine tasks are completed
        val result = viewModel.favoriteLocationsState.first()  // Collect first emitted value
        assertThat(result.isNotEmpty(), `is`(true))
        assertThat(result[0], `is`(location))
    }

}
