package com.demo.demoproject.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.demo.demoproject.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT isFavourite FROM movies_table WHERE movie_id = :movieId")
    suspend fun getIsFavourite(movieId: Int): Boolean?

    @Transaction
    suspend fun upsertMovies(movies: List<Movie>) {
        for (movie in movies) {
            val existingIsFavourite = getIsFavourite(movie.movieId) ?: false
            insertSingle(
                movie.copy(isFavourite = existingIsFavourite)
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingle(movie: Movie)


    @Delete
    suspend fun delete(note: Movie)

    @Query("SELECT * FROM movies_table ORDER BY original_title ASC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE original_title LIKE '%' || :searchQuery || '%' ORDER BY original_title ASC")
    fun searchMoviesByTitle(searchQuery: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE isFavourite = 1 ORDER BY original_title ASC")
    fun getFavouriteMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE original_language IN (:languages) ORDER BY original_title ASC")
    fun filterMoviesByLanguages(languages: List<String>): Flow<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE vote_count IN (:voteCounts) ORDER BY original_title ASC")
    fun filterMoviesByVoteCount(voteCounts: List<Int>): Flow<List<Movie>>
}