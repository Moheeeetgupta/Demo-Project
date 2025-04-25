package com.demo.demoproject.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.demoproject.model.Post

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: List<Post>)

    @Delete
    suspend fun delete(note: Post)

    @Query("SELECT * FROM posts ORDER BY id DESC")
    suspend fun getAllPosts(): List<Post>
}