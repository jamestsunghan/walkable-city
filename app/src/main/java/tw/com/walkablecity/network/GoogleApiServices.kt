package tw.com.walkablecity.network

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tw.com.walkablecity.BuildConfig
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.data.DirectionResult
import tw.com.walkablecity.data.MapImageResult


private const val BASE_URL = "https://maps.googleapis.com/maps/api/"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY})
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface GoogleApiServices{

    @GET("directions/json")
    suspend fun drawPath(
        @Query("origin")origin: String,
        @Query("destination")destination: String,
        @Query("mode")mode: String = "walking",
        @Query("waypoints")waypoints: String,
        @Query("key")key: String = Util.getString(R.string.google_api_key)
    ): DirectionResult

    @GET("staticmap")
    suspend fun getImage(
        @Query("center")center: String,
        @Query("zoom")zoom: String,
        @Query("key")key: String = Util.getString(R.string.google_api_key),
        @Query("path")path: String,
        @Query("size")size: String = "400x400"
    ): MapImageResult
}

object WalkableApi{
    val retrofitService: GoogleApiServices by lazy { retrofit.create(GoogleApiServices::class.java) }
}