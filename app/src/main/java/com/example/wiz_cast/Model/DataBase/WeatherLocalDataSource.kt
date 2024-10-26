import com.example.wiz_cast.Model.DataBase.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun saveFavoriteLocation(favoriteLocation: FavoriteLocation)
    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun deleteFavoriteLocation(lat: Double, lon: Double)
}
