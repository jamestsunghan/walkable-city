package tw.com.walkablecity.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.data.directionresult.DirectionResult
import tw.com.walkablecity.data.weatherresult.WeatherResult

private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

private const val BASE_URL_WEATHER = "https://api.openweathermap.org/data/2.5/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

private val retrofitWeather = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_WEATHER)
    .client(client)
    .build()

interface GoogleApiServices {

    @GET("directions/json")
    suspend fun drawPath(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "walking",
        @Query("waypoints") waypoints: String,
        @Query("key") key: String = Util.getString(R.string.google_api_key)
    ): DirectionResult
}


object WalkableApi {
    val retrofitService: GoogleApiServices by lazy { retrofit.create(GoogleApiServices::class.java) }
}

interface OpenWeatherServices {

    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appKey: String = getString(R.string.open_weather_key),
        @Query("party") part: String = "hourly",
        @Query("units") unit: String = "metric"
    ): WeatherResult
}

object WeatherApi {
    val retrofitServices: OpenWeatherServices by lazy { retrofitWeather.create(OpenWeatherServices::class.java) }
}