package com.demo.demoproject.api

import com.demo.demoproject.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("movies")
    suspend fun getMovies(
        @Query("next_cursor") nextCursor: String?,
        @Query("prev_cursor") prevCursor: String?,
        @Query("limit") limit: Int = 10
    ): MoviesResponse
}