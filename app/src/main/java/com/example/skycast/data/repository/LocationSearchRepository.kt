import com.example.skycast.data.remote.api.NominatimApiService
import com.example.skycast.data.remote.response.NominatimResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationSearchRepository(private val apiService: NominatimApiService) {

    fun searchLocation(query: String): Flow<List<NominatimResponse>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        try {
            val response = apiService.searchLocation(query)
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList()) // Ignore errors for the search bar to keep it smooth
        }
    }
}