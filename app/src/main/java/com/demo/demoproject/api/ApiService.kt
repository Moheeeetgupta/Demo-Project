package com.demo.demoproject.api

import com.demo.demoproject.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}