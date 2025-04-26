package com.demo.demoproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class MoviesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: List<Movie>,
    @SerializedName("pagination")
    val pagination: Pagination
)

@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey
    @SerializedName("movie_id")
    @ColumnInfo(name = "movie_id")
    val movieId: Int,

    @SerializedName("original_title")
    @ColumnInfo(name = "original_title")
    val originalTitle: String?,

    @SerializedName("overview")
    @ColumnInfo(name = "overview")
    val overview: String?,

    @SerializedName("popularity")
    @ColumnInfo(name = "popularity")
    val popularity: Double?,

    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,

    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    val voteCount: Int?,

    @SerializedName("adult")
    @ColumnInfo(name = "adult")
    val adult: Boolean?,

    @SerializedName("original_language")
    @ColumnInfo(name = "original_language")
    val originalLanguage: String?,

    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @ColumnInfo(name = "isFavourite", defaultValue = "0")
    val isFavourite: Boolean = false
)
data class Pagination(
    @SerializedName("next_cursor")
    val nextCursor: String?,

    @SerializedName("prev_cursor")
    val prevCursor: String?,

    @SerializedName("limit")
    val limit: Int         
)
