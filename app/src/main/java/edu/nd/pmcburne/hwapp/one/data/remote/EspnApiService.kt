package edu.nd.pmcburne.hwapp.one.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import edu.nd.pmcburne.hwapp.one.data.model.EspnResponse

// Set up Retrofit API service
interface EspnApiService {
    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScoreboard(
        @Path("gender") gender: String,   // "men" or "women"
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ): EspnResponse

    companion object {
        private const val BASE_URL = "https://ncaa-api.henrygd.me/"

        fun create(): EspnApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EspnApiService::class.java)
        }
    }
}