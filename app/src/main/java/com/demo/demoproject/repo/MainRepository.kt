package com.demo.demoproject.repo

import android.util.Log
import com.demo.demoproject.api.ApiService
import com.demo.demoproject.constants.Constants.MAX_RETRY
import com.demo.demoproject.constants.Constants.RESPONSE_LIMIT
import com.demo.demoproject.constants.Constants.SUCCESS
import com.demo.demoproject.localdb.Dao
import com.demo.demoproject.model.Movie
import kotlinx.coroutines.runBlocking

class MainRepository(
    private val api: ApiService,
    private val dao: Dao
) {
    fun getMovies() {
        runBlocking {
            var nextCursor: String? = null
            var retryCount = 0
            try {
                do {
                    val movieResponse = api.getMovies(nextCursor, null, RESPONSE_LIMIT)
                    if (movieResponse.status == SUCCESS && movieResponse.data.isNotEmpty()) {
                        nextCursor = movieResponse.pagination.nextCursor
                        Log.d("TAG", "getMovies: $nextCursor ${movieResponse.data}")
                        upsertMovies(movieResponse.data)
                    } else {
                        retryCount++
                    }
                } while (nextCursor != null && retryCount < MAX_RETRY)
            }catch (throwable: Throwable){
                Log.d("TAG", "getMovies: ${throwable.message}")
            }
        }

    }


    fun getAllMovies() = dao.getAllMovies()
    fun searchMoviesByTitle(searchQuery: String) = dao.searchMoviesByTitle(searchQuery)
    fun getFavouriteMovies() = dao.getFavouriteMovies()
    fun filterMoviesByLanguages(languages: List<String>) = dao.filterMoviesByLanguages(languages)
    fun filterMoviesByVoteCount(voteCounts: List<Int>) = dao.filterMoviesByVoteCount(voteCounts)
    fun insertSingle(movie: Movie) {
        runBlocking { dao.insertSingle(movie) }
    }
    private suspend fun upsertMovies(movies: List<Movie>) = dao.upsertMovies(movies)
}