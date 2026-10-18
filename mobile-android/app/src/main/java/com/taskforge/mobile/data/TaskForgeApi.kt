package com.taskforge.mobile.data

import com.taskforge.mobile.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface TaskForgeApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("api/tasks")
    suspend fun tasks(
        @Query("status") status: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
        @Query("sort") sort: String = "createdAt,desc"
    ): PageResponse<TaskItem>

    @POST("api/tasks")
    suspend fun createTask(@Body body: TaskRequest): TaskItem

    @PUT("api/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body body: TaskRequest): TaskItem

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Unit>
}

object ApiClient {

    fun create(tokenProvider: () -> String?): TaskForgeApi {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            // Never attach a (possibly stale) token to login/register calls.
            val isAuthCall = request.url.encodedPath.startsWith("/api/auth")
            val token = tokenProvider()
            if (!isAuthCall && token != null) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskForgeApi::class.java)
    }
}
