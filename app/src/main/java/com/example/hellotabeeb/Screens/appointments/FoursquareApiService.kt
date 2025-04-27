package com.example.hellotabeeb.Screens.appointments


import com.example.hellotabeeb.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FoursquareApiService {

@GET("places/search")
suspend fun searchNearbyDoctors(
    @Query("ll") latLng: String,
    @Query("radius") radius: Int,
    @Query("categories") categories: String,
    @Query("limit") limit: Int,
    @Query("fields") fields: String = "name,location,categories,distance,description,tel,website,hours,geocodes",
    @Header("Authorization") apiKey: String = BuildConfig.FOURSQUARE_API_KEY
): PlacesSearchResponse

    companion object {
        fun create(): FoursquareApiService {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v3/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FoursquareApiService::class.java)
        }
    }
}

// Add this class if not already defined
data class PlacesSearchResponse(
    val results: List<FoursquarePlace> = emptyList(),
    val context: Map<String, Any>? = null
)